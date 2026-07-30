import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbTooltip } from '@ng-bootstrap/ng-bootstrap/tooltip';
import { TranslateModule } from '@ngx-translate/core';
import { Observable, finalize } from 'rxjs';

import { AudienceType } from 'app/entities/enumerations/audience-type.model';
import { DeviceType } from 'app/entities/enumerations/device-type.model';
import { PlanCode } from 'app/entities/enumerations/plan-code.model';
import { SignupStatus } from 'app/entities/enumerations/signup-status.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { WaitlistSignupService } from '../service/waitlist-signup.service';
import { IWaitlistSignup } from '../waitlist-signup.model';

import { WaitlistSignupFormGroup, WaitlistSignupFormService } from './waitlist-signup-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-waitlist-signup-update',
  templateUrl: './waitlist-signup-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule, NgbTooltip],
})
export class WaitlistSignupUpdate implements OnInit {
  readonly isSaving = signal(false);
  waitlistSignup: IWaitlistSignup | null = null;
  audienceTypeValues = Object.keys(AudienceType);
  planCodeValues = Object.keys(PlanCode);
  signupStatusValues = Object.keys(SignupStatus);
  deviceTypeValues = Object.keys(DeviceType);

  protected waitlistSignupService = inject(WaitlistSignupService);
  protected waitlistSignupFormService = inject(WaitlistSignupFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: WaitlistSignupFormGroup = this.waitlistSignupFormService.createWaitlistSignupFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ waitlistSignup }) => {
      this.waitlistSignup = waitlistSignup;
      if (waitlistSignup) {
        this.updateForm(waitlistSignup);
      }
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const waitlistSignup = this.waitlistSignupFormService.getWaitlistSignup(this.editForm);
    if (waitlistSignup.id === null) {
      this.subscribeToSaveResponse(this.waitlistSignupService.create(waitlistSignup));
    } else {
      this.subscribeToSaveResponse(this.waitlistSignupService.update(waitlistSignup));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IWaitlistSignup | null>): void {
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

  protected updateForm(waitlistSignup: IWaitlistSignup): void {
    this.waitlistSignup = waitlistSignup;
    this.waitlistSignupFormService.resetForm(this.editForm, waitlistSignup);
  }
}
