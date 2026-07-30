import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable, finalize, map } from 'rxjs';

import { ICareServiceTeaser } from 'app/entities/care-service-teaser/care-service-teaser.model';
import { CareServiceTeaserService } from 'app/entities/care-service-teaser/service/care-service-teaser.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { ServiceHighlightService } from '../service/service-highlight.service';
import { IServiceHighlight } from '../service-highlight.model';

import { ServiceHighlightFormGroup, ServiceHighlightFormService } from './service-highlight-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-service-highlight-update',
  templateUrl: './service-highlight-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class ServiceHighlightUpdate implements OnInit {
  readonly isSaving = signal(false);
  serviceHighlight: IServiceHighlight | null = null;

  careServiceTeasersSharedCollection = signal<ICareServiceTeaser[]>([]);

  protected serviceHighlightService = inject(ServiceHighlightService);
  protected serviceHighlightFormService = inject(ServiceHighlightFormService);
  protected careServiceTeaserService = inject(CareServiceTeaserService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ServiceHighlightFormGroup = this.serviceHighlightFormService.createServiceHighlightFormGroup();

  compareCareServiceTeaser = (o1: ICareServiceTeaser | null, o2: ICareServiceTeaser | null): boolean =>
    this.careServiceTeaserService.compareCareServiceTeaser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ serviceHighlight }) => {
      this.serviceHighlight = serviceHighlight;
      if (serviceHighlight) {
        this.updateForm(serviceHighlight);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const serviceHighlight = this.serviceHighlightFormService.getServiceHighlight(this.editForm);
    if (serviceHighlight.id === null) {
      this.subscribeToSaveResponse(this.serviceHighlightService.create(serviceHighlight));
    } else {
      this.subscribeToSaveResponse(this.serviceHighlightService.update(serviceHighlight));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IServiceHighlight | null>): void {
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

  protected updateForm(serviceHighlight: IServiceHighlight): void {
    this.serviceHighlight = serviceHighlight;
    this.serviceHighlightFormService.resetForm(this.editForm, serviceHighlight);

    this.careServiceTeasersSharedCollection.update(careServiceTeasers =>
      this.careServiceTeaserService.addCareServiceTeaserToCollectionIfMissing<ICareServiceTeaser>(
        careServiceTeasers,
        serviceHighlight.service,
      ),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.careServiceTeaserService
      .query()
      .pipe(map((res: HttpResponse<ICareServiceTeaser[]>) => res.body ?? []))
      .pipe(
        map((careServiceTeasers: ICareServiceTeaser[]) =>
          this.careServiceTeaserService.addCareServiceTeaserToCollectionIfMissing<ICareServiceTeaser>(
            careServiceTeasers,
            this.serviceHighlight?.service,
          ),
        ),
      )
      .subscribe((careServiceTeasers: ICareServiceTeaser[]) => this.careServiceTeasersSharedCollection.set(careServiceTeasers));
  }
}
