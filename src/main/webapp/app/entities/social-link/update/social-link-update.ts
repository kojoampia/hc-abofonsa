import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable, finalize } from 'rxjs';

import { SocialPlatform } from 'app/entities/enumerations/social-platform.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { SocialLinkService } from '../service/social-link.service';
import { ISocialLink } from '../social-link.model';

import { SocialLinkFormGroup, SocialLinkFormService } from './social-link-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-social-link-update',
  templateUrl: './social-link-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class SocialLinkUpdate implements OnInit {
  readonly isSaving = signal(false);
  socialLink: ISocialLink | null = null;
  socialPlatformValues = Object.keys(SocialPlatform);

  protected socialLinkService = inject(SocialLinkService);
  protected socialLinkFormService = inject(SocialLinkFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: SocialLinkFormGroup = this.socialLinkFormService.createSocialLinkFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ socialLink }) => {
      this.socialLink = socialLink;
      if (socialLink) {
        this.updateForm(socialLink);
      }
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const socialLink = this.socialLinkFormService.getSocialLink(this.editForm);
    if (socialLink.id === null) {
      this.subscribeToSaveResponse(this.socialLinkService.create(socialLink));
    } else {
      this.subscribeToSaveResponse(this.socialLinkService.update(socialLink));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ISocialLink | null>): void {
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

  protected updateForm(socialLink: ISocialLink): void {
    this.socialLink = socialLink;
    this.socialLinkFormService.resetForm(this.editForm, socialLink);
  }
}
