// Fails the production build if the generated index.html would be broken by our own CSP.
//
// This exists because it already happened, live. The Content-Security-Policy is `script-src 'self'`
// with no 'unsafe-inline', and Angular's `inlineCritical` optimization emits
//
//     <link rel="stylesheet" href="styles-*.css" media="print" onload="this.media='all'">
//
// That `onload` is an inline event handler, so the browser refuses it, so the stylesheet stays
// `media="print"`, so 243 kB of styles never apply — and the launch page renders as unstyled text.
// Nothing errors and nothing 500s; the page is simply wrong, and only in a browser, which is why a
// suite of HTTP-level checks sailed past it.
//
// `optimization.styles.inlineCritical: false` in angular.json is the fix. This is the guard that
// notices if it is ever turned back on, or if an inline script creeps back into index.html.
//
// Chained onto `webapp:build:prod`, which is what the Maven `prod` profile runs, so CI covers it.

import { readFileSync } from 'node:fs';

const INDEX = 'target/classes/static/index.html';

let html;
try {
  html = readFileSync(INDEX, 'utf8');
} catch {
  console.error(`check-index-csp: ${INDEX} not found — expected to run after \`ng build\`.`);
  process.exit(1);
}

// Comments are not executable, and one of them talks about this very problem.
const withoutComments = html.replace(/<!--[\s\S]*?-->/g, '');

const problems = [];

for (const [, attrs, body] of withoutComments.matchAll(/<script\b([^>]*)>([\s\S]*?)<\/script>/g)) {
  if (!/\bsrc\s*=/.test(attrs) && body.trim()) {
    problems.push(`inline <script> block (${body.trim().slice(0, 60).replace(/\s+/g, ' ')}…)`);
  }
}

for (const [tag] of withoutComments.matchAll(/<[^>]*\son[a-z]+\s*=[^>]*>/g)) {
  problems.push(`inline event handler: ${tag.slice(0, 120)}`);
}

if (problems.length) {
  console.error(`\ncheck-index-csp: ${INDEX} contains markup that \`script-src 'self'\` will refuse:\n`);
  for (const p of problems) console.error(`  - ${p}`);
  console.error(
    [
      '',
      'The browser blocks these silently: the page still renders, so this does not show up as an',
      'error anywhere. A stylesheet deferred behind an onload handler is the dangerous case — it',
      'leaves the site unstyled.',
      '',
      'Fixes: keep optimization.styles.inlineCritical false in angular.json; move inline scripts to',
      'a file and register it under `scripts` there.',
      '',
    ].join('\n'),
  );
  process.exit(1);
}

console.log('check-index-csp: index.html is clean under a strict script-src.');
