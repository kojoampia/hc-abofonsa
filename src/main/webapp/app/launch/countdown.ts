import { ChangeDetectionStrategy, Component, DestroyRef, OnInit, computed, inject, input, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { interval } from 'rxjs';

import { TranslateModule } from '@ngx-translate/core';

/**
 * The launch countdown.
 *
 * <p>The target comes in as an input from the server payload rather than being a constant in the
 * bundle, so moving the launch date is a database edit and not a redeploy.
 */
@Component({
  selector: 'jhi-countdown',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [TranslateModule],
  template: `
    <div class="countdown" role="timer" aria-live="polite" [attr.aria-label]="'launch.countdown.aria' | translate">
      @for (unit of units(); track unit.key) {
        <div class="unit">
          <span class="num">{{ unit.value }}</span>
          <span class="lbl">{{ 'launch.countdown.' + unit.key | translate }}</span>
        </div>
      }
    </div>
    @if (elapsed()) {
      <p class="launch-date live">
        <strong>{{ 'launch.countdown.live' | translate }}</strong>
      </p>
    }
  `,
})
export default class Countdown implements OnInit {
  /** ISO instant to count down to. */
  readonly target = input.required<string>();

  // Declared before the private fields they read, which the member-ordering lint rule requires.
  // Safe because computed() takes a lazy callback: nothing dereferences remainingMs during field
  // initialisation, only on first read from the template.
  readonly elapsed = computed(() => this.remainingMs() === 0);

  readonly units = computed(() => {
    const totalSeconds = Math.floor(this.remainingMs() / 1000);
    return [
      // Days are padded to three: at just over a year out the value is four digits wide for a while
      // and two digits later, and an unpadded number makes the whole row reflow as it ticks.
      { key: 'days', value: pad(Math.floor(totalSeconds / 86400), 3) },
      { key: 'hours', value: pad(Math.floor(totalSeconds / 3600) % 24, 2) },
      { key: 'minutes', value: pad(Math.floor(totalSeconds / 60) % 60, 2) },
      { key: 'seconds', value: pad(totalSeconds % 60, 2) },
    ];
  });

  private readonly destroyRef = inject(DestroyRef);
  private readonly now = signal(Date.now());

  private readonly remainingMs = computed(() => {
    const targetMs = Date.parse(this.target());
    // An unparseable date must not render "NaN" across the hero. Zero collapses to the elapsed
    // state, which at least reads as a coherent page.
    return Number.isNaN(targetMs) ? 0 : Math.max(0, targetMs - this.now());
  });

  ngOnInit(): void {
    interval(1000)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => this.now.set(Date.now()));
  }
}

function pad(value: number, width: number): string {
  return String(value).padStart(width, '0');
}
