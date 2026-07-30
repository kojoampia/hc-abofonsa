import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { ICareServiceTeaser } from 'app/entities/care-service-teaser/care-service-teaser.model';
import { CareServiceTeaserService } from 'app/entities/care-service-teaser/service/care-service-teaser.service';
import { ServiceHighlightService } from '../service/service-highlight.service';
import { IServiceHighlight } from '../service-highlight.model';

import { ServiceHighlightFormService } from './service-highlight-form.service';
import { ServiceHighlightUpdate } from './service-highlight-update';

describe('ServiceHighlight Management Update Component', () => {
  let comp: ServiceHighlightUpdate;
  let fixture: ComponentFixture<ServiceHighlightUpdate>;
  let activatedRoute: ActivatedRoute;
  let serviceHighlightFormService: ServiceHighlightFormService;
  let serviceHighlightService: ServiceHighlightService;
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

    fixture = TestBed.createComponent(ServiceHighlightUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    serviceHighlightFormService = TestBed.inject(ServiceHighlightFormService);
    serviceHighlightService = TestBed.inject(ServiceHighlightService);
    careServiceTeaserService = TestBed.inject(CareServiceTeaserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call CareServiceTeaser query and add missing value', () => {
      const serviceHighlight: IServiceHighlight = { id: 924 };
      const service: ICareServiceTeaser = { id: 28585 };
      serviceHighlight.service = service;

      const careServiceTeaserCollection: ICareServiceTeaser[] = [{ id: 28585 }];
      vitest.spyOn(careServiceTeaserService, 'query').mockReturnValue(of(new HttpResponse({ body: careServiceTeaserCollection })));
      const additionalCareServiceTeasers = [service];
      const expectedCollection: ICareServiceTeaser[] = [...additionalCareServiceTeasers, ...careServiceTeaserCollection];
      vitest.spyOn(careServiceTeaserService, 'addCareServiceTeaserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ serviceHighlight });
      comp.ngOnInit();

      expect(careServiceTeaserService.query).toHaveBeenCalled();
      expect(careServiceTeaserService.addCareServiceTeaserToCollectionIfMissing).toHaveBeenCalledWith(
        careServiceTeaserCollection,
        ...additionalCareServiceTeasers.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.careServiceTeasersSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const serviceHighlight: IServiceHighlight = { id: 924 };
      const service: ICareServiceTeaser = { id: 28585 };
      serviceHighlight.service = service;

      activatedRoute.data = of({ serviceHighlight });
      comp.ngOnInit();

      expect(comp.careServiceTeasersSharedCollection()).toContainEqual(service);
      expect(comp.serviceHighlight).toEqual(serviceHighlight);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IServiceHighlight>();
      const serviceHighlight = { id: 17767 };
      vitest.spyOn(serviceHighlightFormService, 'getServiceHighlight').mockReturnValue(serviceHighlight);
      vitest.spyOn(serviceHighlightService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ serviceHighlight });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(serviceHighlight);
      saveSubject.complete();

      // THEN
      expect(serviceHighlightFormService.getServiceHighlight).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(serviceHighlightService.update).toHaveBeenCalledWith(expect.objectContaining(serviceHighlight));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IServiceHighlight>();
      const serviceHighlight = { id: 17767 };
      vitest.spyOn(serviceHighlightFormService, 'getServiceHighlight').mockReturnValue({ id: null });
      vitest.spyOn(serviceHighlightService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ serviceHighlight: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(serviceHighlight);
      saveSubject.complete();

      // THEN
      expect(serviceHighlightFormService.getServiceHighlight).toHaveBeenCalled();
      expect(serviceHighlightService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IServiceHighlight>();
      const serviceHighlight = { id: 17767 };
      vitest.spyOn(serviceHighlightService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ serviceHighlight });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(serviceHighlightService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareCareServiceTeaser', () => {
      it('should forward to careServiceTeaserService', () => {
        const entity = { id: 28585 };
        const entity2 = { id: 22759 };
        vitest.spyOn(careServiceTeaserService, 'compareCareServiceTeaser');
        comp.compareCareServiceTeaser(entity, entity2);
        expect(careServiceTeaserService.compareCareServiceTeaser).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
