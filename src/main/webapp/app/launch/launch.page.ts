import { DecimalPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DOCUMENT, ElementRef, OnInit, Signal, effect, inject, viewChildren } from '@angular/core';

import { TranslateModule } from '@ngx-translate/core';

import Countdown from './countdown';
import { LaunchContent } from './launch.model';
import { LaunchService } from './launch.service';
import WaitlistForm from './waitlist-form';

/**
 * The launch page: countdown, waitlist, services, plans, pledge hand-off, roadmap and contact.
 *
 * <p>Follows docs/design/abofonsa-countdown.html. Everything it renders comes from
 * {@code GET /api/public/content}, so the copy, the launch date and the tier prices are all
 * database rows rather than markup.
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
  selector: 'jhi-launch',
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './launch.page.html',
  imports: [TranslateModule, DecimalPipe, Countdown, WaitlistForm],
})
export default class LaunchPage implements OnInit {
  readonly currentYear = new Date().getFullYear();

  private readonly launchService = inject(LaunchService);
  private readonly document = inject(DOCUMENT);
  private readonly revealTargets = viewChildren<ElementRef<HTMLElement>>('reveal');
  private readonly observed = new WeakSet<Element>();

  /*
   * Getters rather than fields. These forward signals from LaunchService, so a field initialiser
   * would have to be declared after the private `launchService` it reads — which is exactly what
   * the member-ordering rule forbids for public members. A getter sidesteps the ordering entirely
   * and costs nothing: the signal itself is what the template subscribes to.
   */
  get content(): Signal<LaunchContent | undefined> {
    return this.launchService.content;
  }

  get loading(): Signal<boolean> {
    return this.launchService.loading;
  }

  get failed(): Signal<boolean> {
    return this.launchService.failed;
  }

  constructor() {
    // Reactive rather than ngAfterViewInit. The sections live behind an `@if` on the loaded
    // content, so at ngAfterViewInit the request is still in flight and the query is empty —
    // nothing gets observed, and because .reveal starts at opacity:0 every section below the hero
    // stays permanently invisible. Reading the signal inside an effect re-runs it when the
    // elements actually appear.
    effect(() => this.observeReveals(this.revealTargets().map(ref => ref.nativeElement)));
  }

  ngOnInit(): void {
    this.launchService.recordEvent('PAGE_VIEW');
  }

  /** Counts the outbound click before the browser follows the link to fund.abofonsa.com. */
  pledgeClick(tierCode?: string): void {
    this.launchService.recordEvent('PLEDGE_CTA_CLICK', tierCode);
  }

  socialClick(platform: string): void {
    this.launchService.recordEvent('SOCIAL_CLICK', platform);
  }

  contactClick(channel: string): void {
    this.launchService.recordEvent('CONTACT_CLICK', channel);
  }

  /**
   * The bare host of a URL, for showing "jojoaddison.net" rather than the full
   * "https://jojoaddison.net/". Falls back to the input when it will not parse, so a mistyped value
   * in the settings row renders as itself instead of disappearing from the page entirely.
   */
  hostOf(url: string): string {
    try {
      return new URL(url).host.replace(/^www\./, '');
    } catch {
      return url;
    }
  }

  /**
   * Reveal-on-scroll, matching the design.
   *
   * <p>Skipped entirely when the visitor has asked for reduced motion, and skipped when
   * IntersectionObserver is unavailable — in both cases the sections are left visible rather than
   * animated in, because the failure mode of a missing observer is a permanently blank page.
   */
  private observeReveals(all: HTMLElement[]): void {
    const targets = all.filter(el => !this.observed.has(el));
    if (targets.length === 0) {
      return;
    }
    targets.forEach(el => this.observed.add(el));

    const view = this.document.defaultView;
    if (!view?.IntersectionObserver || view.matchMedia('(prefers-reduced-motion: reduce)').matches) {
      targets.forEach(el => el.classList.add('in'));
      return;
    }

    const observer = new view.IntersectionObserver(
      entries => {
        entries.forEach(entry => {
          if (entry.isIntersecting) {
            entry.target.classList.add('in');
            observer.unobserve(entry.target);
          }
        });
      },
      { threshold: 0.12 },
    );
    targets.forEach(el => observer.observe(el));
  }
}
