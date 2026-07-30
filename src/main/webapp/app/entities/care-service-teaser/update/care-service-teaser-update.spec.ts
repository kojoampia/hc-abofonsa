import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { ICareServiceTeaser } from '../care-service-teaser.model';
import { CareServiceTeaserService } from '../service/care-service-teaser.service';

import { CareServiceTeaserFormService } from './care-service-teaser-form.service';
import { CareServiceTeaserUpdate } from './care-service-teaser-update';

describe('CareServiceTeaser Management Update Component', () => {
  let comp: CareServiceTeaserUpdate;
  let fixture: ComponentFixture<CareServiceTeaserUpdate>;
  let activatedRoute: ActivatedRoute;
  let careServiceTeaserFormService: CareServiceTeaserFormService;
  let careServiceTeaserService: CareServiceTeaserService;

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

    fixture = TestBed.createComponent(CareServiceTeaserUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    careServiceTeaserFormService = TestBed.inject(CareServiceTeaserFormService);
    careServiceTeaserService = TestBed.inject(CareServiceTeaserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const careServiceTeaser: ICareServiceTeaser = { id: 22759 };

      activatedRoute.data = of({ careServiceTeaser });
      comp.ngOnInit();

      expect(comp.careServiceTeaser).toEqual(careServiceTeaser);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ICareServiceTeaser>();
      const careServiceTeaser = { id: 28585 };
      vitest.spyOn(careServiceTeaserFormService, 'getCareServiceTeaser').mockReturnValue(careServiceTeaser);
      vitest.spyOn(careServiceTeaserService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ careServiceTeaser });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(careServiceTeaser);
      saveSubject.complete();

      // THEN
      expect(careServiceTeaserFormService.getCareServiceTeaser).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(careServiceTeaserService.update).toHaveBeenCalledWith(expect.objectContaining(careServiceTeaser));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ICareServiceTeaser>();
      const careServiceTeaser = { id: 28585 };
      vitest.spyOn(careServiceTeaserFormService, 'getCareServiceTeaser').mockReturnValue({ id: null });
      vitest.spyOn(careServiceTeaserService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ careServiceTeaser: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(careServiceTeaser);
      saveSubject.complete();

      // THEN
      expect(careServiceTeaserFormService.getCareServiceTeaser).toHaveBeenCalled();
      expect(careServiceTeaserService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ICareServiceTeaser>();
      const careServiceTeaser = { id: 28585 };
      vitest.spyOn(careServiceTeaserService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ careServiceTeaser });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(careServiceTeaserService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
