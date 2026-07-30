import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable, finalize } from 'rxjs';

import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { PlanCode } from 'app/entities/enumerations/plan-code.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { AlertErrorModel } from 'app/shared/alert/alert-error.model';
import { TranslateDirective } from 'app/shared/language';
import { ICarePlanTeaser } from '../care-plan-teaser.model';
import { CarePlanTeaserService } from '../service/care-plan-teaser.service';

import { CarePlanTeaserFormGroup, CarePlanTeaserFormService } from './care-plan-teaser-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-care-plan-teaser-update',
  templateUrl: './care-plan-teaser-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class CarePlanTeaserUpdate implements OnInit {
  readonly isSaving = signal(false);
  carePlanTeaser: ICarePlanTeaser | null = null;
  planCodeValues = Object.keys(PlanCode);

  protected dataUtils = inject(DataUtils);
  protected eventManager = inject(EventManager);
  protected carePlanTeaserService = inject(CarePlanTeaserService);
  protected carePlanTeaserFormService = inject(CarePlanTeaserFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: CarePlanTeaserFormGroup = this.carePlanTeaserFormService.createCarePlanTeaserFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ carePlanTeaser }) => {
      this.carePlanTeaser = carePlanTeaser;
      if (carePlanTeaser) {
        this.updateForm(carePlanTeaser);
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
    const carePlanTeaser = this.carePlanTeaserFormService.getCarePlanTeaser(this.editForm);
    if (carePlanTeaser.id === null) {
      this.subscribeToSaveResponse(this.carePlanTeaserService.create(carePlanTeaser));
    } else {
      this.subscribeToSaveResponse(this.carePlanTeaserService.update(carePlanTeaser));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ICarePlanTeaser | null>): void {
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

  protected updateForm(carePlanTeaser: ICarePlanTeaser): void {
    this.carePlanTeaser = carePlanTeaser;
    this.carePlanTeaserFormService.resetForm(this.editForm, carePlanTeaser);
  }
}
