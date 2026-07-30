import {
  entityConfirmDeleteButtonSelector,
  entityCreateButtonSelector,
  entityCreateCancelButtonSelector,
  entityCreateSaveButtonSelector,
  entityDeleteButtonSelector,
  entityDetailsBackButtonSelector,
  entityDetailsButtonSelector,
  entityEditButtonSelector,
  entityTableSelector,
} from '../../support/entity';

describe('PledgeTierTeaser e2e test', () => {
  const pledgeTierTeaserPageUrl = '/pledge-tier-teaser';
  const pledgeTierTeaserPageUrlPattern = new RegExp('/pledge-tier-teaser(\\?.*)?$');
  let username: string;
  let password: string;
  const pledgeTierTeaserSample = {
    name: 'winged opposite coolly',
    amount: 10766.79,
    currency: 'alt',
    handoffUrl: 'if pinstripe',
    displayOrder: 24433,
    published: false,
  };

  let pledgeTierTeaser;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/pledge-tier-teasers+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/pledge-tier-teasers').as('postEntityRequest');
    cy.intercept('DELETE', '/api/pledge-tier-teasers/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (pledgeTierTeaser) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/pledge-tier-teasers/${pledgeTierTeaser.id}`,
      }).then(() => {
        pledgeTierTeaser = undefined;
      });
    }
  });

  it('PledgeTierTeasers menu should load PledgeTierTeasers page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('pledge-tier-teaser');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('PledgeTierTeaser').should('exist');
    cy.url().should('match', pledgeTierTeaserPageUrlPattern);
  });

  describe('PledgeTierTeaser page', () => {
    it('should have translated page title', () => {
      cy.visit(pledgeTierTeaserPageUrl);
      cy.getEntityHeading('PledgeTierTeaser').should('not.contain', 'abofonsaPreviewApp.pledgeTierTeaser.home.title');
    });

    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(pledgeTierTeaserPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create PledgeTierTeaser page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/pledge-tier-teaser/new$'));
        cy.getEntityCreateUpdateHeading('PledgeTierTeaser');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', pledgeTierTeaserPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/pledge-tier-teasers',
          body: pledgeTierTeaserSample,
        }).then(({ body }) => {
          pledgeTierTeaser = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/pledge-tier-teasers+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [pledgeTierTeaser],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(pledgeTierTeaserPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details PledgeTierTeaser page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('pledgeTierTeaser');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', pledgeTierTeaserPageUrlPattern);
      });

      it('edit button click should load edit PledgeTierTeaser page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('PledgeTierTeaser');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', pledgeTierTeaserPageUrlPattern);
      });

      it('edit button click should load edit PledgeTierTeaser page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('PledgeTierTeaser');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', pledgeTierTeaserPageUrlPattern);
      });

      it('last delete button click should delete instance of PledgeTierTeaser', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('pledgeTierTeaser').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', pledgeTierTeaserPageUrlPattern);

        pledgeTierTeaser = undefined;
      });
    });
  });

  describe('new PledgeTierTeaser page', () => {
    beforeEach(() => {
      cy.visit(pledgeTierTeaserPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('PledgeTierTeaser');
    });

    it('should create an instance of PledgeTierTeaser', () => {
      cy.get(`[data-cy="name"]`).type('brisk fidget');
      cy.get(`[data-cy="name"]`).should('have.value', 'brisk fidget');

      cy.get(`[data-cy="blurb"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="blurb"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="amount"]`).type('30548.56');
      cy.get(`[data-cy="amount"]`).should('have.value', '30548.56');

      cy.get(`[data-cy="currency"]`).type('mid');
      cy.get(`[data-cy="currency"]`).should('have.value', 'mid');

      cy.get(`[data-cy="voucherValue"]`).type('7105.21');
      cy.get(`[data-cy="voucherValue"]`).should('have.value', '7105.21');

      cy.get(`[data-cy="handoffUrl"]`).type('thorn a');
      cy.get(`[data-cy="handoffUrl"]`).should('have.value', 'thorn a');

      cy.get(`[data-cy="displayOrder"]`).type('11771');
      cy.get(`[data-cy="displayOrder"]`).should('have.value', '11771');

      cy.get(`[data-cy="published"]`).should('not.be.checked');
      cy.get(`[data-cy="published"]`).click();
      cy.get(`[data-cy="published"]`).should('be.checked');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        pledgeTierTeaser = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', pledgeTierTeaserPageUrlPattern);
    });
  });
});
