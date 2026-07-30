import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable, finalize } from 'rxjs';

import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { AlertErrorModel } from 'app/shared/alert/alert-error.model';
import { TranslateDirective } from 'app/shared/language';
import { ICareServiceTeaser } from '../care-service-teaser.model';
import { CareServiceTeaserService } from '../service/care-service-teaser.service';

import { CareServiceTeaserFormGroup, CareServiceTeaserFormService } from './care-service-teaser-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-care-service-teaser-update',
  templateUrl: './care-service-teaser-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class CareServiceTeaserUpdate implements OnInit {
  readonly isSaving = signal(false);
  careServiceTeaser: ICareServiceTeaser | null = null;

  protected dataUtils = inject(DataUtils);
  protected eventManager = inject(EventManager);
  protected careServiceTeaserService = inject(CareServiceTeaserService);
  protected careServiceTeaserFormService = inject(CareServiceTeaserFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: CareServiceTeaserFormGroup = this.careServiceTeaserFormService.createCareServiceTeaserFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ careServiceTeaser }) => {
      this.careServiceTeaser = careServiceTeaser;
      if (careServiceTeaser) {
        this.updateForm(careServiceTeaser);
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
    const careServiceTeaser = this.careServiceTeaserFormService.getCareServiceTeaser(this.editForm);
    if (careServiceTeaser.id === null) {
      this.subscribeToSaveResponse(this.careServiceTeaserService.create(careServiceTeaser));
    } else {
      this.subscribeToSaveResponse(this.careServiceTeaserService.update(careServiceTeaser));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ICareServiceTeaser | null>): void {
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

  protected updateForm(careServiceTeaser: ICareServiceTeaser): void {
    this.careServiceTeaser = careServiceTeaser;
    this.careServiceTeaserFormService.resetForm(this.editForm, careServiceTeaser);
  }
}
