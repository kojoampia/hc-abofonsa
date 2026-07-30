import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { ILaunchSetting } from '../launch-setting.model';
import { LaunchSettingService } from '../service/launch-setting.service';

import { LaunchSettingFormService } from './launch-setting-form.service';
import { LaunchSettingUpdate } from './launch-setting-update';

describe('LaunchSetting Management Update Component', () => {
  let comp: LaunchSettingUpdate;
  let fixture: ComponentFixture<LaunchSettingUpdate>;
  let activatedRoute: ActivatedRoute;
  let launchSettingFormService: LaunchSettingFormService;
  let launchSettingService: LaunchSettingService;

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

    fixture = TestBed.createComponent(LaunchSettingUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    launchSettingFormService = TestBed.inject(LaunchSettingFormService);
    launchSettingService = TestBed.inject(LaunchSettingService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const launchSetting: ILaunchSetting = { id: 22879 };

      activatedRoute.data = of({ launchSetting });
      comp.ngOnInit();

      expect(comp.launchSetting).toEqual(launchSetting);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ILaunchSetting>();
      const launchSetting = { id: 31747 };
      vitest.spyOn(launchSettingFormService, 'getLaunchSetting').mockReturnValue(launchSetting);
      vitest.spyOn(launchSettingService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ launchSetting });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(launchSetting);
      saveSubject.complete();

      // THEN
      expect(launchSettingFormService.getLaunchSetting).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(launchSettingService.update).toHaveBeenCalledWith(expect.objectContaining(launchSetting));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ILaunchSetting>();
      const launchSetting = { id: 31747 };
      vitest.spyOn(launchSettingFormService, 'getLaunchSetting').mockReturnValue({ id: null });
      vitest.spyOn(launchSettingService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ launchSetting: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(launchSetting);
      saveSubject.complete();

      // THEN
      expect(launchSettingFormService.getLaunchSetting).toHaveBeenCalled();
      expect(launchSettingService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ILaunchSetting>();
      const launchSetting = { id: 31747 };
      vitest.spyOn(launchSettingService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ launchSetting });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(launchSettingService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
