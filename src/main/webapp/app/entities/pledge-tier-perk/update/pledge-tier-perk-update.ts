import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable, finalize, map } from 'rxjs';

import { IPledgeTierTeaser } from 'app/entities/pledge-tier-teaser/pledge-tier-teaser.model';
import { PledgeTierTeaserService } from 'app/entities/pledge-tier-teaser/service/pledge-tier-teaser.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IPledgeTierPerk } from '../pledge-tier-perk.model';
import { PledgeTierPerkService } from '../service/pledge-tier-perk.service';

import { PledgeTierPerkFormGroup, PledgeTierPerkFormService } from './pledge-tier-perk-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-pledge-tier-perk-update',
  templateUrl: './pledge-tier-perk-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class PledgeTierPerkUpdate implements OnInit {
  readonly isSaving = signal(false);
  pledgeTierPerk: IPledgeTierPerk | null = null;

  pledgeTierTeasersSharedCollection = signal<IPledgeTierTeaser[]>([]);

  protected pledgeTierPerkService = inject(PledgeTierPerkService);
  protected pledgeTierPerkFormService = inject(PledgeTierPerkFormService);
  protected pledgeTierTeaserService = inject(PledgeTierTeaserService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: PledgeTierPerkFormGroup = this.pledgeTierPerkFormService.createPledgeTierPerkFormGroup();

  comparePledgeTierTeaser = (o1: IPledgeTierTeaser | null, o2: IPledgeTierTeaser | null): boolean =>
    this.pledgeTierTeaserService.comparePledgeTierTeaser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ pledgeTierPerk }) => {
      this.pledgeTierPerk = pledgeTierPerk;
      if (pledgeTierPerk) {
        this.updateForm(pledgeTierPerk);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const pledgeTierPerk = this.pledgeTierPerkFormService.getPledgeTierPerk(this.editForm);
    if (pledgeTierPerk.id === null) {
      this.subscribeToSaveResponse(this.pledgeTierPerkService.create(pledgeTierPerk));
    } else {
      this.subscribeToSaveResponse(this.pledgeTierPerkService.update(pledgeTierPerk));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IPledgeTierPerk | null>): void {
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

  protected updateForm(pledgeTierPerk: IPledgeTierPerk): void {
    this.pledgeTierPerk = pledgeTierPerk;
    this.pledgeTierPerkFormService.resetForm(this.editForm, pledgeTierPerk);

    this.pledgeTierTeasersSharedCollection.update(pledgeTierTeasers =>
      this.pledgeTierTeaserService.addPledgeTierTeaserToCollectionIfMissing<IPledgeTierTeaser>(pledgeTierTeasers, pledgeTierPerk.tier),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.pledgeTierTeaserService
      .query()
      .pipe(map((res: HttpResponse<IPledgeTierTeaser[]>) => res.body ?? []))
      .pipe(
        map((pledgeTierTeasers: IPledgeTierTeaser[]) =>
          this.pledgeTierTeaserService.addPledgeTierTeaserToCollectionIfMissing<IPledgeTierTeaser>(
            pledgeTierTeasers,
            this.pledgeTierPerk?.tier,
          ),
        ),
      )
      .subscribe((pledgeTierTeasers: IPledgeTierTeaser[]) => this.pledgeTierTeasersSharedCollection.set(pledgeTierTeasers));
  }
}
