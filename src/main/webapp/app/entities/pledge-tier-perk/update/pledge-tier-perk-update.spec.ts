import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IPledgeTierTeaser } from 'app/entities/pledge-tier-teaser/pledge-tier-teaser.model';
import { PledgeTierTeaserService } from 'app/entities/pledge-tier-teaser/service/pledge-tier-teaser.service';
import { IPledgeTierPerk } from '../pledge-tier-perk.model';
import { PledgeTierPerkService } from '../service/pledge-tier-perk.service';

import { PledgeTierPerkFormService } from './pledge-tier-perk-form.service';
import { PledgeTierPerkUpdate } from './pledge-tier-perk-update';

describe('PledgeTierPerk Management Update Component', () => {
  let comp: PledgeTierPerkUpdate;
  let fixture: ComponentFixture<PledgeTierPerkUpdate>;
  let activatedRoute: ActivatedRoute;
  let pledgeTierPerkFormService: PledgeTierPerkFormService;
  let pledgeTierPerkService: PledgeTierPerkService;
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

    fixture = TestBed.createComponent(PledgeTierPerkUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    pledgeTierPerkFormService = TestBed.inject(PledgeTierPerkFormService);
    pledgeTierPerkService = TestBed.inject(PledgeTierPerkService);
    pledgeTierTeaserService = TestBed.inject(PledgeTierTeaserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call PledgeTierTeaser query and add missing value', () => {
      const pledgeTierPerk: IPledgeTierPerk = { id: 31987 };
      const tier: IPledgeTierTeaser = { id: 29268 };
      pledgeTierPerk.tier = tier;

      const pledgeTierTeaserCollection: IPledgeTierTeaser[] = [{ id: 29268 }];
      vitest.spyOn(pledgeTierTeaserService, 'query').mockReturnValue(of(new HttpResponse({ body: pledgeTierTeaserCollection })));
      const additionalPledgeTierTeasers = [tier];
      const expectedCollection: IPledgeTierTeaser[] = [...additionalPledgeTierTeasers, ...pledgeTierTeaserCollection];
      vitest.spyOn(pledgeTierTeaserService, 'addPledgeTierTeaserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ pledgeTierPerk });
      comp.ngOnInit();

      expect(pledgeTierTeaserService.query).toHaveBeenCalled();
      expect(pledgeTierTeaserService.addPledgeTierTeaserToCollectionIfMissing).toHaveBeenCalledWith(
        pledgeTierTeaserCollection,
        ...additionalPledgeTierTeasers.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.pledgeTierTeasersSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const pledgeTierPerk: IPledgeTierPerk = { id: 31987 };
      const tier: IPledgeTierTeaser = { id: 29268 };
      pledgeTierPerk.tier = tier;

      activatedRoute.data = of({ pledgeTierPerk });
      comp.ngOnInit();

      expect(comp.pledgeTierTeasersSharedCollection()).toContainEqual(tier);
      expect(comp.pledgeTierPerk).toEqual(pledgeTierPerk);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IPledgeTierPerk>();
      const pledgeTierPerk = { id: 23265 };
      vitest.spyOn(pledgeTierPerkFormService, 'getPledgeTierPerk').mockReturnValue(pledgeTierPerk);
      vitest.spyOn(pledgeTierPerkService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ pledgeTierPerk });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(pledgeTierPerk);
      saveSubject.complete();

      // THEN
      expect(pledgeTierPerkFormService.getPledgeTierPerk).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(pledgeTierPerkService.update).toHaveBeenCalledWith(expect.objectContaining(pledgeTierPerk));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IPledgeTierPerk>();
      const pledgeTierPerk = { id: 23265 };
      vitest.spyOn(pledgeTierPerkFormService, 'getPledgeTierPerk').mockReturnValue({ id: null });
      vitest.spyOn(pledgeTierPerkService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ pledgeTierPerk: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(pledgeTierPerk);
      saveSubject.complete();

      // THEN
      expect(pledgeTierPerkFormService.getPledgeTierPerk).toHaveBeenCalled();
      expect(pledgeTierPerkService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IPledgeTierPerk>();
      const pledgeTierPerk = { id: 23265 };
      vitest.spyOn(pledgeTierPerkService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ pledgeTierPerk });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(pledgeTierPerkService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('comparePledgeTierTeaser', () => {
      it('should forward to pledgeTierTeaserService', () => {
        const entity = { id: 29268 };
        const entity2 = { id: 4226 };
        vitest.spyOn(pledgeTierTeaserService, 'comparePledgeTierTeaser');
        comp.comparePledgeTierTeaser(entity, entity2);
        expect(pledgeTierTeaserService.comparePledgeTierTeaser).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
