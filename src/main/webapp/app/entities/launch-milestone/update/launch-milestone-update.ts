import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbInputDatepicker } from '@ng-bootstrap/ng-bootstrap/datepicker';
import { TranslateModule } from '@ngx-translate/core';
import { Observable, finalize } from 'rxjs';

import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { AlertErrorModel } from 'app/shared/alert/alert-error.model';
import { TranslateDirective } from 'app/shared/language';
import { ILaunchMilestone } from '../launch-milestone.model';
import { LaunchMilestoneService } from '../service/launch-milestone.service';

import { LaunchMilestoneFormGroup, LaunchMilestoneFormService } from './launch-milestone-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-launch-milestone-update',
  templateUrl: './launch-milestone-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule, NgbInputDatepicker],
})
export class LaunchMilestoneUpdate implements OnInit {
  readonly isSaving = signal(false);
  launchMilestone: ILaunchMilestone | null = null;

  protected dataUtils = inject(DataUtils);
  protected eventManager = inject(EventManager);
  protected launchMilestoneService = inject(LaunchMilestoneService);
  protected launchMilestoneFormService = inject(LaunchMilestoneFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: LaunchMilestoneFormGroup = this.launchMilestoneFormService.createLaunchMilestoneFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ launchMilestone }) => {
      this.launchMilestone = launchMilestone;
      if (launchMilestone) {
        this.updateForm(launchMilestone);
      }
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(
          new EventWithContent<AlertErrorModel>('abofonsaPreviewApp.error', { ...err, key: `error.file.${err.key}` }),
        ),
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const launchMilestone = this.launchMilestoneFormService.getLaunchMilestone(this.editForm);
    if (launchMilestone.id === null) {
      this.subscribeToSaveResponse(this.launchMilestoneService.create(launchMilestone));
    } else {
      this.subscribeToSaveResponse(this.launchMilestoneService.update(launchMilestone));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ILaunchMilestone | null>): void {
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

  protected updateForm(launchMilestone: ILaunchMilestone): void {
    this.launchMilestone = launchMilestone;
    this.launchMilestoneFormService.resetForm(this.editForm, launchMilestone);
  }
}
