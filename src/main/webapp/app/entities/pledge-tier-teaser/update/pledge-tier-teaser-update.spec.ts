import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IPledgeTierTeaser } from '../pledge-tier-teaser.model';
import { PledgeTierTeaserService } from '../service/pledge-tier-teaser.service';

import { PledgeTierTeaserFormService } from './pledge-tier-teaser-form.service';
import { PledgeTierTeaserUpdate } from './pledge-tier-teaser-update';

describe('PledgeTierTeaser Management Update Component', () => {
  let comp: PledgeTierTeaserUpdate;
  let fixture: ComponentFixture<PledgeTierTeaserUpdate>;
  let activatedRoute: ActivatedRoute;
  let pledgeTierTeaserFormService: PledgeTierTeaserFormService;
  let pledgeTierTeaserService: PledgeTierTeaserService;

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

    fixture = TestBed.createComponent(PledgeTierTeaserUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    pledgeTierTeaserFormService = TestBed.inject(PledgeTierTeaserFormService);
    pledgeTierTeaserService = TestBed.inject(PledgeTierTeaserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const pledgeTierTeaser: IPledgeTierTeaser = { id: 4226 };

      activatedRoute.data = of({ pledgeTierTeaser });
      comp.ngOnInit();

      expect(comp.pledgeTierTeaser).toEqual(pledgeTierTeaser);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IPledgeTierTeaser>();
      const pledgeTierTeaser = { id: 29268 };
      vitest.spyOn(pledgeTierTeaserFormService, 'getPledgeTierTeaser').mockReturnValue(pledgeTierTeaser);
      vitest.spyOn(pledgeTierTeaserService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ pledgeTierTeaser });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(pledgeTierTeaser);
      saveSubject.complete();

      // THEN
      expect(pledgeTierTeaserFormService.getPledgeTierTeaser).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(pledgeTierTeaserService.update).toHaveBeenCalledWith(expect.objectContaining(pledgeTierTeaser));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IPledgeTierTeaser>();
      const pledgeTierTeaser = { id: 29268 };
      vitest.spyOn(pledgeTierTeaserFormService, 'getPledgeTierTeaser').mockReturnValue({ id: null });
      vitest.spyOn(pledgeTierTeaserService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ pledgeTierTeaser: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(pledgeTierTeaser);
      saveSubject.complete();

      // THEN
      expect(pledgeTierTeaserFormService.getPledgeTierTeaser).toHaveBeenCalled();
      expect(pledgeTierTeaserService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IPledgeTierTeaser>();
      const pledgeTierTeaser = { id: 29268 };
      vitest.spyOn(pledgeTierTeaserService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ pledgeTierTeaser });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(pledgeTierTeaserService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
