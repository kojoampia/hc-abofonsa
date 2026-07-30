import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faArrowLeft, faPencilAlt } from '@fortawesome/free-solid-svg-icons';
import { TranslateModule } from '@ngx-translate/core';
import { of } from 'rxjs';

import { PledgeTierPerkDetail } from './pledge-tier-perk-detail';

describe('PledgeTierPerk Management Detail Component', () => {
  let comp: PledgeTierPerkDetail;
  let fixture: ComponentFixture<PledgeTierPerkDetail>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TranslateModule.forRoot()],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./pledge-tier-perk-detail').then(m => m.PledgeTierPerkDetail),
              resolve: { pledgeTierPerk: () => of({ id: 23265 }) },
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
    fixture = TestBed.createComponent(PledgeTierPerkDetail);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load pledgeTierPerk on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', PledgeTierPerkDetail);

      // THEN
      expect(instance.pledgeTierPerk()).toEqual(expect.objectContaining({ id: 23265 }));
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
