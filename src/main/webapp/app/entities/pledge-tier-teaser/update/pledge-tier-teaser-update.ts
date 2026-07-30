import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable, finalize } from 'rxjs';

import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { PledgeTierCode } from 'app/entities/enumerations/pledge-tier-code.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { AlertErrorModel } from 'app/shared/alert/alert-error.model';
import { TranslateDirective } from 'app/shared/language';
import { IPledgeTierTeaser } from '../pledge-tier-teaser.model';
import { PledgeTierTeaserService } from '../service/pledge-tier-teaser.service';

import { PledgeTierTeaserFormGroup, PledgeTierTeaserFormService } from './pledge-tier-teaser-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-pledge-tier-teaser-update',
  templateUrl: './pledge-tier-teaser-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class PledgeTierTeaserUpdate implements OnInit {
  readonly isSaving = signal(false);
  pledgeTierTeaser: IPledgeTierTeaser | null = null;
  pledgeTierCodeValues = Object.keys(PledgeTierCode);

  protected dataUtils = inject(DataUtils);
  protected eventManager = inject(EventManager);
  protected pledgeTierTeaserService = inject(PledgeTierTeaserService);
  protected pledgeTierTeaserFormService = inject(PledgeTierTeaserFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: PledgeTierTeaserFormGroup = this.pledgeTierTeaserFormService.createPledgeTierTeaserFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ pledgeTierTeaser }) => {
      this.pledgeTierTeaser = pledgeTierTeaser;
      if (pledgeTierTeaser) {
        this.updateForm(pledgeTierTeaser);
      }
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(
          new EventWithContent<AlertErrorModel>('abofonsaPreviewApp.error', { ...err, key: `error.file.${err.key}` }),
        ),
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const pledgeTierTeaser = this.pledgeTierTeaserFormService.getPledgeTierTeaser(this.editForm);
    if (pledgeTierTeaser.id === null) {
      this.subscribeToSaveResponse(this.pledgeTierTeaserService.create(pledgeTierTeaser));
    } else {
      this.subscribeToSaveResponse(this.pledgeTierTeaserService.update(pledgeTierTeaser));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IPledgeTierTeaser | null>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving.set(false);
  }

  protected updateForm(pledgeTierTeaser: IPledgeTierTeaser): void {
    this.pledgeTierTeaser = pledgeTierTeaser;
    this.pledgeTierTeaserFormService.resetForm(this.editForm, pledgeTierTeaser);
  }
}
