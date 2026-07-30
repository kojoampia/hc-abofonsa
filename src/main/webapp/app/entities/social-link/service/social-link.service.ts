import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { ISocialLink, NewSocialLink } from '../social-link.model';

export type PartialUpdateSocialLink = Partial<ISocialLink> & Pick<ISocialLink, 'id'>;

@Injectable()
export class SocialLinksService {
  readonly socialLinksParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly socialLinksResource = httpResource<ISocialLink[]>(() => {
    const params = this.socialLinksParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of socialLink that have been fetched. It is updated when the socialLinksResource emits a new value.
   * In case of error while fetching the socialLinks, the signal is set to an empty array.
   */
  readonly socialLinks = computed(() => (this.socialLinksResource.hasValue() ? this.socialLinksResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/social-links');
}

@Injectable({ providedIn: 'root' })
export class SocialLinkService extends SocialLinksService {
  protected readonly http = inject(HttpClient);

  create(socialLink: NewSocialLink): Observable<ISocialLink> {
    return this.http.post<ISocialLink>(this.resourceUrl, socialLink);
  }

  update(socialLink: ISocialLink): Observable<ISocialLink> {
    return this.http.put<ISocialLink>(`${this.resourceUrl}/${encodeURIComponent(this.getSocialLinkIdentifier(socialLink))}`, socialLink);
  }

  partialUpdate(socialLink: PartialUpdateSocialLink): Observable<ISocialLink> {
    return this.http.patch<ISocialLink>(`${this.resourceUrl}/${encodeURIComponent(this.getSocialLinkIdentifier(socialLink))}`, socialLink);
  }

  find(id: number): Observable<ISocialLink> {
    return this.http.get<ISocialLink>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<ISocialLink[]>> {
    const options = createRequestOption(req);
    return this.http.get<ISocialLink[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getSocialLinkIdentifier(socialLink: Pick<ISocialLink, 'id'>): number {
    return socialLink.id;
  }

  compareSocialLink(o1: Pick<ISocialLink, 'id'> | null, o2: Pick<ISocialLink, 'id'> | null): boolean {
    return o1 && o2 ? this.getSocialLinkIdentifier(o1) === this.getSocialLinkIdentifier(o2) : o1 === o2;
  }

  addSocialLinkToCollectionIfMissing<Type extends Pick<ISocialLink, 'id'>>(
    socialLinkCollection: Type[],
    ...socialLinksToCheck: (Type | null | undefined)[]
  ): Type[] {
    const socialLinks: Type[] = socialLinksToCheck.filter(isPresent);
    if (socialLinks.length > 0) {
      const socialLinkCollectionIdentifiers = socialLinkCollection.map(socialLinkItem => this.getSocialLinkIdentifier(socialLinkItem));
      const socialLinksToAdd = socialLinks.filter(socialLinkItem => {
        const socialLinkIdentifier = this.getSocialLinkIdentifier(socialLinkItem);
        if (socialLinkCollectionIdentifiers.includes(socialLinkIdentifier)) {
          return false;
        }
        socialLinkCollectionIdentifiers.push(socialLinkIdentifier);
        return true;
      });
      return [...socialLinksToAdd, ...socialLinkCollection];
    }
    return socialLinkCollection;
  }
}
