import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faArrowLeft, faPencilAlt } from '@fortawesome/free-solid-svg-icons';
import { TranslateModule } from '@ngx-translate/core';
import { of } from 'rxjs';

import { WaitlistSignupDetail } from './waitlist-signup-detail';

describe('WaitlistSignup Management Detail Component', () => {
  let comp: WaitlistSignupDetail;
  let fixture: ComponentFixture<WaitlistSignupDetail>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TranslateModule.forRoot()],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./waitlist-signup-detail').then(m => m.WaitlistSignupDetail),
              resolve: { waitlistSignup: () => of({ id: 9304 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    });
    const library = TestBed.inject(FaIconLibrary);
    library.addIcons(faArrowLeft);
    library.addIcons(faPencilAlt);
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(WaitlistSignupDetail);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load waitlistSignup on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', WaitlistSignupDetail);

      // THEN
      expect(instance.waitlistSignup()).toEqual(expect.objectContaining({ id: 9304 }));
    });
  });

  describe('PreviousState', () => {
    it('should navigate to previous state', () => {
      vitest.spyOn(globalThis.history, 'back');
      comp.previousState();
      expect(globalThis.history.back).toHaveBeenCalled();
    });
  });
});
