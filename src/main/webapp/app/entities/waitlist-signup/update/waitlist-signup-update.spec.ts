import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { WaitlistSignupService } from '../service/waitlist-signup.service';
import { IWaitlistSignup } from '../waitlist-signup.model';

import { WaitlistSignupFormService } from './waitlist-signup-form.service';
import { WaitlistSignupUpdate } from './waitlist-signup-update';

describe('WaitlistSignup Management Update Component', () => {
  let comp: WaitlistSignupUpdate;
  let fixture: ComponentFixture<WaitlistSignupUpdate>;
  let activatedRoute: ActivatedRoute;
  let waitlistSignupFormService: WaitlistSignupFormService;
  let waitlistSignupService: WaitlistSignupService;

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

    fixture = TestBed.createComponent(WaitlistSignupUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    waitlistSignupFormService = TestBed.inject(WaitlistSignupFormService);
    waitlistSignupService = TestBed.inject(WaitlistSignupService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const waitlistSignup: IWaitlistSignup = { id: 21600 };

      activatedRoute.data = of({ waitlistSignup });
      comp.ngOnInit();

      expect(comp.waitlistSignup).toEqual(waitlistSignup);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IWaitlistSignup>();
      const waitlistSignup = { id: 9304 };
      vitest.spyOn(waitlistSignupFormService, 'getWaitlistSignup').mockReturnValue(waitlistSignup);
      vitest.spyOn(waitlistSignupService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ waitlistSignup });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(waitlistSignup);
      saveSubject.complete();

      // THEN
      expect(waitlistSignupFormService.getWaitlistSignup).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(waitlistSignupService.update).toHaveBeenCalledWith(expect.objectContaining(waitlistSignup));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IWaitlistSignup>();
      const waitlistSignup = { id: 9304 };
      vitest.spyOn(waitlistSignupFormService, 'getWaitlistSignup').mockReturnValue({ id: null });
      vitest.spyOn(waitlistSignupService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ waitlistSignup: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(waitlistSignup);
      saveSubject.complete();

      // THEN
      expect(waitlistSignupFormService.getWaitlistSignup).toHaveBeenCalled();
      expect(waitlistSignupService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IWaitlistSignup>();
      const waitlistSignup = { id: 9304 };
      vitest.spyOn(waitlistSignupService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ waitlistSignup });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(waitlistSignupService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
