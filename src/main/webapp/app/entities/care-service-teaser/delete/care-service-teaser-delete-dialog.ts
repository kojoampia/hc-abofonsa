import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { TranslateModule } from '@ngx-translate/core';

import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { ICareServiceTeaser } from '../care-service-teaser.model';
import { CareServiceTeaserService } from '../service/care-service-teaser.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './care-service-teaser-delete-dialog.html',
  imports: [TranslateDirective, TranslateModule, FormsModule, FontAwesomeModule, AlertError],
})
export class CareServiceTeaserDeleteDialog {
  careServiceTeaser?: ICareServiceTeaser;

  protected readonly careServiceTeaserService = inject(CareServiceTeaserService);
  protected readonly activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.careServiceTeaserService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
