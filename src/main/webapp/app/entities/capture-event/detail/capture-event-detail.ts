import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbTooltip } from '@ng-bootstrap/ng-bootstrap/tooltip';
import { TranslateModule } from '@ngx-translate/core';

import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { TranslateDirective } from 'app/shared/language';
import { ICaptureEvent } from '../capture-event.model';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-capture-event-detail',
  templateUrl: './capture-event-detail.html',
  imports: [
    FontAwesomeModule,
    NgbTooltip,
    Alert,
    AlertError,
    TranslateDirective,
    TranslateModule,
    RouterLink,
    FormatMediumDatetimePipe,
    FormatMediumDatePipe,
  ],
})
export class CaptureEventDetail {
  readonly captureEvent = input<ICaptureEvent | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
