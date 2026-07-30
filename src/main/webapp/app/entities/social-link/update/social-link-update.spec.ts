import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { SocialLinkService } from '../service/social-link.service';
import { ISocialLink } from '../social-link.model';

import { SocialLinkFormService } from './social-link-form.service';
import { SocialLinkUpdate } from './social-link-update';

describe('SocialLink Management Update Component', () => {
  let comp: SocialLinkUpdate;
  let fixture: ComponentFixture<SocialLinkUpdate>;
  let activatedRoute: ActivatedRoute;
  let socialLinkFormService: SocialLinkFormService;
  let socialLinkService: SocialLinkService;

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

    fixture = TestBed.createComponent(SocialLinkUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    socialLinkFormService = TestBed.inject(SocialLinkFormService);
    socialLinkService = TestBed.inject(SocialLinkService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const socialLink: ISocialLink = { id: 24820 };

      activatedRoute.data = of({ socialLink });
      comp.ngOnInit();

      expect(comp.socialLink).toEqual(socialLink);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ISocialLink>();
      const socialLink = { id: 18647 };
      vitest.spyOn(socialLinkFormService, 'getSocialLink').mockReturnValue(socialLink);
      vitest.spyOn(socialLinkService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ socialLink });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(socialLink);
      saveSubject.complete();

      // THEN
      expect(socialLinkFormService.getSocialLink).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(socialLinkService.update).toHaveBeenCalledWith(expect.objectContaining(socialLink));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ISocialLink>();
      const socialLink = { id: 18647 };
      vitest.spyOn(socialLinkFormService, 'getSocialLink').mockReturnValue({ id: null });
      vitest.spyOn(socialLinkService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ socialLink: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(socialLink);
      saveSubject.complete();

      // THEN
      expect(socialLinkFormService.getSocialLink).toHaveBeenCalled();
      expect(socialLinkService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ISocialLink>();
      const socialLink = { id: 18647 };
      vitest.spyOn(socialLinkService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ socialLink });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(socialLinkService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
