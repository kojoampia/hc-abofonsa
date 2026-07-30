import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable, finalize } from 'rxjs';

import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { ILaunchSetting } from '../launch-setting.model';
import { LaunchSettingService } from '../service/launch-setting.service';

import { LaunchSettingFormGroup, LaunchSettingFormService } from './launch-setting-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-launch-setting-update',
  templateUrl: './launch-setting-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class LaunchSettingUpdate implements OnInit {
  readonly isSaving = signal(false);
  launchSetting: ILaunchSetting | null = null;

  protected launchSettingService = inject(LaunchSettingService);
  protected launchSettingFormService = inject(LaunchSettingFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: LaunchSettingFormGroup = this.launchSettingFormService.createLaunchSettingFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ launchSetting }) => {
      this.launchSetting = launchSetting;
      if (launchSetting) {
        this.updateForm(launchSetting);
      }
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const launchSetting = this.launchSettingFormService.getLaunchSetting(this.editForm);
    if (launchSetting.id === null) {
      this.subscribeToSaveResponse(this.launchSettingService.create(launchSetting));
    } else {
      this.subscribeToSaveResponse(this.launchSettingService.update(launchSetting));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ILaunchSetting | null>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving.set(false);
  }

  protected updateForm(launchSetting: ILaunchSetting): void {
    this.launchSetting = launchSetting;
    this.launchSettingFormService.resetForm(this.editForm, launchSetting);
  }
}
