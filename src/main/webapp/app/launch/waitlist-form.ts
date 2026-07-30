import { ChangeDetectionStrategy, Component, DOCUMENT, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { TranslateModule } from '@ngx-translate/core';

import { LaunchService } from './launch.service';

type FormState = 'idle' | 'submitting' | 'done' | 'error';

/**
 * The waitlist capture form: one input, one button, matching the design.
 *
 * <p>Two anti-bot measures are carried in the payload and enforced server-side — a hidden honeypot
 * field, and how long the form was on screen before submit. Neither is enforced here; client-side
 * checks are advisory, and a bot posting straight to the API never runs this code at all.
 */
@Component({
  selector: 'jhi-waitlist-form',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [FormsModule, TranslateModule],
  template: `
    <form class="signup" (ngSubmit)="submit()" novalidate>
      <div class="field">
        <label for="waitlist-email" class="visually-hidden">{{ 'launch.waitlist.emailLabel' | translate }}</label>
        <input
          id="waitlist-email"
          name="email"
          type="email"
          inputmode="email"
          autocomplete="email"
          [placeholder]="'launch.waitlist.placeholder' | translate"
          [(ngModel)]="email"
          [disabled]="state() === 'submitting' || state() === 'done'"
          required
        />

        <!--
          The honeypot. Hidden from sight, from the accessibility tree and from tab order, and
          marked not-autofillable so a password manager cannot fill it on a real person's behalf —
          which would otherwise turn this into a way to reject genuine signups.
        -->
        <input class="hp-field" type="text" name="company" tabindex="-1" autocomplete="off" aria-hidden="true" [(ngModel)]="honeypot" />

        <button class="btn" type="submit" [disabled]="state() === 'submitting' || state() === 'done'">
          @switch (state()) {
            @case ('submitting') {
              {{ 'launch.waitlist.submitting' | translate }}
            }
            @case ('done') {
              {{ 'launch.waitlist.added' | translate }}
            }
            @default {
              {{ 'launch.waitlist.submit' | translate }}
            }
          }
        </button>
      </div>

      <p class="form-note" [class.ok]="state() === 'done'" [class.err]="state() === 'error'" aria-live="polite">
        {{ note() | translate }}
      </p>
    </form>
  `,
})
export default class WaitlistForm {
  email = '';
  honeypot = '';

  readonly state = signal<FormState>('idle');
  readonly note = signal('launch.waitlist.hint');

  private readonly launchService = inject(LaunchService);
  private readonly document = inject(DOCUMENT);
  private readonly mountedAt = Date.now();

  submit(): void {
    if (this.state() === 'submitting' || this.state() === 'done') {
      return;
    }

    const email = this.email.trim();
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/.test(email)) {
      this.state.set('error');
      this.note.set('launch.waitlist.invalid');
      return;
    }

    this.state.set('submitting');
    this.note.set('launch.waitlist.hint');

    this.launchService
      .submitWaitlist({
        email,
        consent: true,
        company: this.honeypot,
        dwellMs: Date.now() - this.mountedAt,
        locale: this.document.documentElement.lang || 'en',
        sourcePage: this.document.location.pathname,
      })
      .subscribe({
        next: receipt => {
          this.state.set('done');
          this.note.set(receipt.status === 'ALREADY_CONFIRMED' ? 'launch.waitlist.alreadyOn' : 'launch.waitlist.success');
        },
        error: response => {
          this.state.set('error');
          // 429 is the one failure worth distinguishing: it is the only one where trying again
          // later actually helps, and telling somebody to "try again" when it will never work is
          // worse than saying nothing.
          this.note.set(response?.status === 429 ? 'launch.waitlist.throttled' : 'launch.waitlist.failed');
        },
      });
  }
}
