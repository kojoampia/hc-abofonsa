import { ChangeDetectionStrategy, Component, computed, inject, input, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';

import { LaunchService } from './launch.service';

type Phase = 'ready' | 'working' | 'ok' | 'invalid';

/**
 * Where the confirmation and unsubscribe links in an email land.
 *
 * <p>The link used to point straight at the API, which did the work on a `GET` and redirected here.
 * Mail clients and security gateways routinely prefetch links, so a scanner could complete somebody
 * else's double opt-in — and the row would then record consent that no human ever gave. This page
 * asks first and posts on a click, which is the only part of the exchange a prefetcher will not do.
 *
 * <p>It also fixes a plain bug. The emailed link has always pointed at `/confirm`, and the router
 * had `/confirmed` and `/unsubscribed` but no `/confirm` at all — so every confirmation link in
 * every message ever sent landed on the 404 page.
 */
// The launch stylesheet is a global (angular.json → styles), shared with the launch page and the
// result page; every selector in it is nested under `.abf-launch`.
@Component({
  selector: 'jhi-opt-in-action',
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
        <span class="pill">{{ 'launch.optIn.' + kind() + '.pill' | translate }}</span>

        @switch (phase()) {
          @case ('ok') {
            <h1>{{ 'launch.optIn.' + doneKey() + '.heading' | translate }}</h1>
            <p class="lede">{{ 'launch.optIn.' + doneKey() + '.body' | translate }}</p>
          }
          @case ('invalid') {
            <h1>{{ 'launch.optIn.invalid.heading' | translate }}</h1>
            <p class="lede">{{ 'launch.optIn.invalid.body' | translate }}</p>
          }
          @default {
            <h1>{{ 'launch.optIn.' + kind() + '.prompt.heading' | translate }}</h1>
            <p class="lede">{{ 'launch.optIn.' + kind() + '.prompt.body' | translate }}</p>
            <p>
              <button type="button" class="btn-lg" data-cy="optInConfirm" [disabled]="phase() === 'working'" (click)="act()">
                {{ 'launch.optIn.' + kind() + '.prompt.action' | translate }}
              </button>
            </p>
          }
        }

        <p class="launch-date">
          <a routerLink="/">{{ 'launch.optIn.back' | translate }}</a>
        </p>
      </div>
    </div>
  `,
})
export default class OptInActionPage {
  /** 'confirm' or 'unsubscribe', bound from the route path. */
  readonly kind = input.required<string>();
  /** The secret from the emailed link. */
  readonly token = input<string>('');

  readonly phase = signal<Phase>('ready');

  /** Which finished-state copy to show — the result page's wording, reused. */
  readonly doneKey = computed(() => (this.kind() === 'unsubscribe' ? 'unsubscribed' : 'confirmed'));

  private readonly launchService = inject(LaunchService);

  act(): void {
    if (!this.token()) {
      this.phase.set('invalid');
      return;
    }
    this.phase.set('working');

    const request =
      this.kind() === 'unsubscribe'
        ? this.launchService.unsubscribeWaitlist(this.token())
        : this.launchService.confirmWaitlist(this.token());

    request.subscribe({
      next: result => this.phase.set(result.status === 'ok' ? 'ok' : 'invalid'),
      // A network failure is not a rejected token, but there is nothing useful to tell somebody
      // reading an email about the difference, and the link stays good either way.
      error: () => this.phase.set('invalid'),
    });
  }
}
