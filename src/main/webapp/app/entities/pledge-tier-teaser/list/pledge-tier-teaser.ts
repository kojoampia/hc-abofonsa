import { ChangeDetectionStrategy, Component, OnInit, effect, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Data, ParamMap, Router, RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { TranslateModule } from '@ngx-translate/core';
import { Subscription, combineLatest, filter, tap } from 'rxjs';

import { DEFAULT_SORT_DATA, ITEM_DELETED_EVENT, SORT } from 'app/config/navigation.constants';
import { DataUtils } from 'app/core/util/data-util.service';
import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { SortByDirective, SortDirective, SortService, type SortState, sortStateSignal } from 'app/shared/sort';
import { PledgeTierTeaserDeleteDialog } from '../delete/pledge-tier-teaser-delete-dialog';
import { IPledgeTierTeaser } from '../pledge-tier-teaser.model';
import { PledgeTierTeaserService } from '../service/pledge-tier-teaser.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-pledge-tier-teaser',
  templateUrl: './pledge-tier-teaser.html',
  imports: [
    RouterLink,
    FormsModule,
    FontAwesomeModule,
    AlertError,
    Alert,
    SortDirective,
    SortByDirective,
    TranslateDirective,
    TranslateModule,
  ],
})
export class PledgeTierTeaser implements OnInit {
  subscription: Subscription | null = null;
  readonly pledgeTierTeasers = signal<IPledgeTierTeaser[]>([]);

  sortState = sortStateSignal({});

  readonly router = inject(Router);
  protected readonly pledgeTierTeaserService = inject(PledgeTierTeaserService);
  // eslint-disable-next-line @typescript-eslint/member-ordering
  readonly isLoading = this.pledgeTierTeaserService.pledgeTierTeasersResource.isLoading;
  protected readonly activatedRoute = inject(ActivatedRoute);
  protected readonly sortService = inject(SortService);
  protected dataUtils = inject(DataUtils);
  protected modalService = inject(NgbModal);

  constructor() {
    effect(() => {
      this.pledgeTierTeasers.set(this.fillComponentAttributesFromResponseBody([...this.pledgeTierTeaserService.pledgeTierTeasers()]));
    });
  }

  trackId = (item: IPledgeTierTeaser): number => this.pledgeTierTeaserService.getPledgeTierTeaserIdentifier(item);

  ngOnInit(): void {
    this.subscription = combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data])
      .pipe(
        tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
        tap(() => {
          if (this.pledgeTierTeasers().length === 0) {
            this.load();
          }
        }),
      )
      .subscribe();
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    return this.dataUtils.openFile(base64String, contentType);
  }

  delete(pledgeTierTeaser: IPledgeTierTeaser): void {
    const modalRef = this.modalService.open(PledgeTierTeaserDeleteDialog, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.pledgeTierTeaser = pledgeTierTeaser;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed
      .pipe(
        filter(reason => reason === ITEM_DELETED_EVENT),
        tap(() => this.load()),
      )
      .subscribe();
  }

  load(): void {
    this.queryBackend();
  }

  navigateToWithComponentValues(event: SortState): void {
    this.handleNavigation(event);
  }

  protected fillComponentAttributeFromRoute(params: ParamMap, data: Data): void {
    this.sortState.set(this.sortService.parseSortParam(params.get(SORT) ?? data[DEFAULT_SORT_DATA]));
  }

  protected refineData(data: IPledgeTierTeaser[]): IPledgeTierTeaser[] {
    const { predicate, order } = this.sortState();
    return predicate && order ? data.sort(this.sortService.startSort({ predicate, order })) : data;
  }

  protected fillComponentAttributesFromResponseBody(data: IPledgeTierTeaser[]): IPledgeTierTeaser[] {
    return this.refineData(data);
  }

  protected queryBackend(): void {
    const queryObject: any = {
      sort: this.sortService.buildSortParam(this.sortState()),
    };
    this.pledgeTierTeaserService.pledgeTierTeasersParams.set(queryObject);
  }

  protected handleNavigation(sortState: SortState): void {
    const queryParamsObj = {
      sort: this.sortService.buildSortParam(sortState),
    };

    this.router.navigate(['./'], {
      relativeTo: this.activatedRoute,
      queryParams: queryParamsObj,
    });
  }
}
