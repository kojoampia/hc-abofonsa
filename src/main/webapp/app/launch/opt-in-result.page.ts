import { ChangeDetectionStrategy, Component, computed, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';

/**
 * Where the confirm and unsubscribe endpoints redirect to.
 *
 * <p>Both outcomes share a component because they are the same page with different words: a short
 * message and a way back. The endpoints do the work and redirect here; this renders the result.
 */
// The launch page's stylesheet is a global (angular.json → styles), not a component style.
//
// Three reasons. It is 13.75 kB, which blows the 4 kB per-component budget the production build
// enforces. Two components use it, and as a component style it was injected twice. And as a
// global it applies through emulated encapsulation to the countdown and waitlist child
// components, which is what ViewEncapsulation.None was working around — so that is gone too.
//
// Safe because every selector in it is nested under `.abf-launch`; nothing escapes to the
// Bootstrap the admin screens use.
@Component({
  selector: 'jhi-opt-in-result',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [TranslateModule, RouterLink],
  template: `
    <div class="abf-launch">
      <div class="bg" aria-hidden="true">
        <div class="orb a"></div>
        <div class="orb c"></div>
        <div class="grid-lines"></div>
      </div>

      <div class="wrap hero">
        <span class="pill">{{ 'launch.optIn.' + outcome() + '.pill' | translate }}</span>
        <h1>{{ 'launch.optIn.' + outcome() + '.heading' | translate }}</h1>
        <p class="lede">{{ 'launch.optIn.' + outcome() + '.body' | translate }}</p>
        <p class="launch-date">
          <a routerLink="/">{{ 'launch.optIn.back' | translate }}</a>
        </p>
      </div>
    </div>
  `,
})
export default class OptInResultPage {
  /** 'confirmed' or 'unsubscribed', bound from the route path. */
  readonly kind = input.required<string>();
  /** 'ok' or 'invalid', bound from the query string the redirect carries. */
  readonly status = input<string>('ok');

  readonly outcome = computed(() => {
    const kind = this.kind() === 'unsubscribed' ? 'unsubscribed' : 'confirmed';
    // Anything that is not an explicit success is treated as an invalid token. A tampered or
    // expired link should say so rather than congratulate somebody on a subscription they do not
    // have.
    return this.status() === 'ok' ? kind : 'invalid';
  });
}
