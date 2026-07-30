import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { ILaunchMilestone } from '../launch-milestone.model';
import { LaunchMilestoneService } from '../service/launch-milestone.service';

import { LaunchMilestoneFormService } from './launch-milestone-form.service';
import { LaunchMilestoneUpdate } from './launch-milestone-update';

describe('LaunchMilestone Management Update Component', () => {
  let comp: LaunchMilestoneUpdate;
  let fixture: ComponentFixture<LaunchMilestoneUpdate>;
  let activatedRoute: ActivatedRoute;
  let launchMilestoneFormService: LaunchMilestoneFormService;
  let launchMilestoneService: LaunchMilestoneService;

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

    fixture = TestBed.createComponent(LaunchMilestoneUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    launchMilestoneFormService = TestBed.inject(LaunchMilestoneFormService);
    launchMilestoneService = TestBed.inject(LaunchMilestoneService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const launchMilestone: ILaunchMilestone = { id: 12820 };

      activatedRoute.data = of({ launchMilestone });
      comp.ngOnInit();

      expect(comp.launchMilestone).toEqual(launchMilestone);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ILaunchMilestone>();
      const launchMilestone = { id: 2676 };
      vitest.spyOn(launchMilestoneFormService, 'getLaunchMilestone').mockReturnValue(launchMilestone);
      vitest.spyOn(launchMilestoneService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ launchMilestone });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(launchMilestone);
      saveSubject.complete();

      // THEN
      expect(launchMilestoneFormService.getLaunchMilestone).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(launchMilestoneService.update).toHaveBeenCalledWith(expect.objectContaining(launchMilestone));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ILaunchMilestone>();
      const launchMilestone = { id: 2676 };
      vitest.spyOn(launchMilestoneFormService, 'getLaunchMilestone').mockReturnValue({ id: null });
      vitest.spyOn(launchMilestoneService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ launchMilestone: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(launchMilestone);
      saveSubject.complete();

      // THEN
      expect(launchMilestoneFormService.getLaunchMilestone).toHaveBeenCalled();
      expect(launchMilestoneService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ILaunchMilestone>();
      const launchMilestone = { id: 2676 };
      vitest.spyOn(launchMilestoneService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ launchMilestone });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(launchMilestoneService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
