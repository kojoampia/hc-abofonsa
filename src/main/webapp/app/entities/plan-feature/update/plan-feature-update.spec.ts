import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { ICarePlanTeaser } from 'app/entities/care-plan-teaser/care-plan-teaser.model';
import { CarePlanTeaserService } from 'app/entities/care-plan-teaser/service/care-plan-teaser.service';
import { IPlanFeature } from '../plan-feature.model';
import { PlanFeatureService } from '../service/plan-feature.service';

import { PlanFeatureFormService } from './plan-feature-form.service';
import { PlanFeatureUpdate } from './plan-feature-update';

describe('PlanFeature Management Update Component', () => {
  let comp: PlanFeatureUpdate;
  let fixture: ComponentFixture<PlanFeatureUpdate>;
  let activatedRoute: ActivatedRoute;
  let planFeatureFormService: PlanFeatureFormService;
  let planFeatureService: PlanFeatureService;
  let carePlanTeaserService: CarePlanTeaserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TranslateModule.forRoot()],
      providers: [
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    });

    fixture = TestBed.createComponent(PlanFeatureUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    planFeatureFormService = TestBed.inject(PlanFeatureFormService);
    planFeatureService = TestBed.inject(PlanFeatureService);
    carePlanTeaserService = TestBed.inject(CarePlanTeaserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call CarePlanTeaser query and add missing value', () => {
      const planFeature: IPlanFeature = { id: 16120 };
      const plan: ICarePlanTeaser = { id: 16096 };
      planFeature.plan = plan;

      const carePlanTeaserCollection: ICarePlanTeaser[] = [{ id: 16096 }];
      vitest.spyOn(carePlanTeaserService, 'query').mockReturnValue(of(new HttpResponse({ body: carePlanTeaserCollection })));
      const additionalCarePlanTeasers = [plan];
      const expectedCollection: ICarePlanTeaser[] = [...additionalCarePlanTeasers, ...carePlanTeaserCollection];
      vitest.spyOn(carePlanTeaserService, 'addCarePlanTeaserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ planFeature });
      comp.ngOnInit();

      expect(carePlanTeaserService.query).toHaveBeenCalled();
      expect(carePlanTeaserService.addCarePlanTeaserToCollectionIfMissing).toHaveBeenCalledWith(
        carePlanTeaserCollection,
        ...additionalCarePlanTeasers.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.carePlanTeasersSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const planFeature: IPlanFeature = { id: 16120 };
      const plan: ICarePlanTeaser = { id: 16096 };
      planFeature.plan = plan;

      activatedRoute.data = of({ planFeature });
      comp.ngOnInit();

      expect(comp.carePlanTeasersSharedCollection()).toContainEqual(plan);
      expect(comp.planFeature).toEqual(planFeature);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IPlanFeature>();
      const planFeature = { id: 22331 };
      vitest.spyOn(planFeatureFormService, 'getPlanFeature').mockReturnValue(planFeature);
      vitest.spyOn(planFeatureService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ planFeature });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(planFeature);
      saveSubject.complete();

      // THEN
      expect(planFeatureFormService.getPlanFeature).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(planFeatureService.update).toHaveBeenCalledWith(expect.objectContaining(planFeature));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IPlanFeature>();
      const planFeature = { id: 22331 };
      vitest.spyOn(planFeatureFormService, 'getPlanFeature').mockReturnValue({ id: null });
      vitest.spyOn(planFeatureService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ planFeature: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(planFeature);
      saveSubject.complete();

      // THEN
      expect(planFeatureFormService.getPlanFeature).toHaveBeenCalled();
      expect(planFeatureService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IPlanFeature>();
      const planFeature = { id: 22331 };
      vitest.spyOn(planFeatureService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ planFeature });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(planFeatureService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareCarePlanTeaser', () => {
      it('should forward to carePlanTeaserService', () => {
        const entity = { id: 16096 };
        const entity2 = { id: 3687 };
        vitest.spyOn(carePlanTeaserService, 'compareCarePlanTeaser');
        comp.compareCarePlanTeaser(entity, entity2);
        expect(carePlanTeaserService.compareCarePlanTeaser).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
