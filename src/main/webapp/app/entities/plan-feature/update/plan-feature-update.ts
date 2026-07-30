import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable, finalize, map } from 'rxjs';

import { ICarePlanTeaser } from 'app/entities/care-plan-teaser/care-plan-teaser.model';
import { CarePlanTeaserService } from 'app/entities/care-plan-teaser/service/care-plan-teaser.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IPlanFeature } from '../plan-feature.model';
import { PlanFeatureService } from '../service/plan-feature.service';

import { PlanFeatureFormGroup, PlanFeatureFormService } from './plan-feature-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-plan-feature-update',
  templateUrl: './plan-feature-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class PlanFeatureUpdate implements OnInit {
  readonly isSaving = signal(false);
  planFeature: IPlanFeature | null = null;

  carePlanTeasersSharedCollection = signal<ICarePlanTeaser[]>([]);

  protected planFeatureService = inject(PlanFeatureService);
  protected planFeatureFormService = inject(PlanFeatureFormService);
  protected carePlanTeaserService = inject(CarePlanTeaserService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: PlanFeatureFormGroup = this.planFeatureFormService.createPlanFeatureFormGroup();

  compareCarePlanTeaser = (o1: ICarePlanTeaser | null, o2: ICarePlanTeaser | null): boolean =>
    this.carePlanTeaserService.compareCarePlanTeaser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ planFeature }) => {
      this.planFeature = planFeature;
      if (planFeature) {
        this.updateForm(planFeature);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const planFeature = this.planFeatureFormService.getPlanFeature(this.editForm);
    if (planFeature.id === null) {
      this.subscribeToSaveResponse(this.planFeatureService.create(planFeature));
    } else {
      this.subscribeToSaveResponse(this.planFeatureService.update(planFeature));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IPlanFeature | null>): void {
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

  protected updateForm(planFeature: IPlanFeature): void {
    this.planFeature = planFeature;
    this.planFeatureFormService.resetForm(this.editForm, planFeature);

    this.carePlanTeasersSharedCollection.update(carePlanTeasers =>
      this.carePlanTeaserService.addCarePlanTeaserToCollectionIfMissing<ICarePlanTeaser>(carePlanTeasers, planFeature.plan),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.carePlanTeaserService
      .query()
      .pipe(map((res: HttpResponse<ICarePlanTeaser[]>) => res.body ?? []))
      .pipe(
        map((carePlanTeasers: ICarePlanTeaser[]) =>
          this.carePlanTeaserService.addCarePlanTeaserToCollectionIfMissing<ICarePlanTeaser>(carePlanTeasers, this.planFeature?.plan),
        ),
      )
      .subscribe((carePlanTeasers: ICarePlanTeaser[]) => this.carePlanTeasersSharedCollection.set(carePlanTeasers));
  }
}
