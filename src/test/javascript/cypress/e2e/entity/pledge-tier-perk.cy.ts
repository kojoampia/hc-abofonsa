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

describe('PledgeTierPerk e2e test', () => {
  const pledgeTierPerkPageUrl = '/pledge-tier-perk';
  const pledgeTierPerkPageUrlPattern = new RegExp('/pledge-tier-perk(\\?.*)?$');
  let username: string;
  let password: string;
  const pledgeTierPerkSample = { label: 'uh-huh wedding', displayOrder: 18057 };

  let pledgeTierPerk;
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
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/pledge-tier-teasers',
      body: {
        name: 'jury',
        blurb: 'Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=',
        amount: 11688.55,
        currency: 'onc',
        voucherValue: 12257.23,
        handoffUrl: 'accredit blank',
        displayOrder: 5789,
        published: true,
      },
    }).then(({ body }) => {
      pledgeTierTeaser = body;
    });
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/pledge-tier-perks+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/pledge-tier-perks').as('postEntityRequest');
    cy.intercept('DELETE', '/api/pledge-tier-perks/*').as('deleteEntityRequest');
  });

  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/pledge-tier-teasers', {
      statusCode: 200,
      body: [pledgeTierTeaser],
    });
  });

  afterEach(() => {
    if (pledgeTierPerk) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/pledge-tier-perks/${pledgeTierPerk.id}`,
      }).then(() => {
        pledgeTierPerk = undefined;
      });
    }
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

  it('PledgeTierPerks menu should load PledgeTierPerks page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('pledge-tier-perk');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('PledgeTierPerk').should('exist');
    cy.url().should('match', pledgeTierPerkPageUrlPattern);
  });

  describe('PledgeTierPerk page', () => {
    it('should have translated page title', () => {
      cy.visit(pledgeTierPerkPageUrl);
      cy.getEntityHeading('PledgeTierPerk').should('not.contain', 'abofonsaPreviewApp.pledgeTierPerk.home.title');
    });

    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(pledgeTierPerkPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create PledgeTierPerk page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/pledge-tier-perk/new$'));
        cy.getEntityCreateUpdateHeading('PledgeTierPerk');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', pledgeTierPerkPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/pledge-tier-perks',
          body: {
            ...pledgeTierPerkSample,
            tier: pledgeTierTeaser,
          },
        }).then(({ body }) => {
          pledgeTierPerk = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/pledge-tier-perks+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [pledgeTierPerk],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(pledgeTierPerkPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details PledgeTierPerk page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('pledgeTierPerk');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', pledgeTierPerkPageUrlPattern);
      });

      it('edit button click should load edit PledgeTierPerk page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('PledgeTierPerk');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', pledgeTierPerkPageUrlPattern);
      });

      it('edit button click should load edit PledgeTierPerk page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('PledgeTierPerk');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', pledgeTierPerkPageUrlPattern);
      });

      it('last delete button click should delete instance of PledgeTierPerk', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('pledgeTierPerk').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', pledgeTierPerkPageUrlPattern);

        pledgeTierPerk = undefined;
      });
    });
  });

  describe('new PledgeTierPerk page', () => {
    beforeEach(() => {
      cy.visit(pledgeTierPerkPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('PledgeTierPerk');
    });

    it('should create an instance of PledgeTierPerk', () => {
      cy.get(`[data-cy="label"]`).type('cannibalise');
      cy.get(`[data-cy="label"]`).should('have.value', 'cannibalise');

      cy.get(`[data-cy="displayOrder"]`).type('7591');
      cy.get(`[data-cy="displayOrder"]`).should('have.value', '7591');

      cy.get(`[data-cy="tier"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        pledgeTierPerk = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', pledgeTierPerkPageUrlPattern);
    });
  });
});
