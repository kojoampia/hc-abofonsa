import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';

import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IPledgeTierPerk } from '../pledge-tier-perk.model';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-pledge-tier-perk-detail',
  templateUrl: './pledge-tier-perk-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, TranslateDirective, TranslateModule, RouterLink],
})
export class PledgeTierPerkDetail {
  readonly pledgeTierPerk = input<IPledgeTierPerk | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
