import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { ICarePlanTeaser } from '../care-plan-teaser.model';
import { CarePlanTeaserService } from '../service/care-plan-teaser.service';

import { CarePlanTeaserFormService } from './care-plan-teaser-form.service';
import { CarePlanTeaserUpdate } from './care-plan-teaser-update';

describe('CarePlanTeaser Management Update Component', () => {
  let comp: CarePlanTeaserUpdate;
  let fixture: ComponentFixture<CarePlanTeaserUpdate>;
  let activatedRoute: ActivatedRoute;
  let carePlanTeaserFormService: CarePlanTeaserFormService;
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

    fixture = TestBed.createComponent(CarePlanTeaserUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    carePlanTeaserFormService = TestBed.inject(CarePlanTeaserFormService);
    carePlanTeaserService = TestBed.inject(CarePlanTeaserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const carePlanTeaser: ICarePlanTeaser = { id: 3687 };

      activatedRoute.data = of({ carePlanTeaser });
      comp.ngOnInit();

      expect(comp.carePlanTeaser).toEqual(carePlanTeaser);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ICarePlanTeaser>();
      const carePlanTeaser = { id: 16096 };
      vitest.spyOn(carePlanTeaserFormService, 'getCarePlanTeaser').mockReturnValue(carePlanTeaser);
      vitest.spyOn(carePlanTeaserService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ carePlanTeaser });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(carePlanTeaser);
      saveSubject.complete();

      // THEN
      expect(carePlanTeaserFormService.getCarePlanTeaser).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(carePlanTeaserService.update).toHaveBeenCalledWith(expect.objectContaining(carePlanTeaser));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ICarePlanTeaser>();
      const carePlanTeaser = { id: 16096 };
      vitest.spyOn(carePlanTeaserFormService, 'getCarePlanTeaser').mockReturnValue({ id: null });
      vitest.spyOn(carePlanTeaserService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ carePlanTeaser: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(carePlanTeaser);
      saveSubject.complete();

      // THEN
      expect(carePlanTeaserFormService.getCarePlanTeaser).toHaveBeenCalled();
      expect(carePlanTeaserService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ICarePlanTeaser>();
      const carePlanTeaser = { id: 16096 };
      vitest.spyOn(carePlanTeaserService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ carePlanTeaser });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(carePlanTeaserService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
