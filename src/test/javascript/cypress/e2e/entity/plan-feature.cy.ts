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

describe('PlanFeature e2e test', () => {
  const planFeaturePageUrl = '/plan-feature';
  const planFeaturePageUrlPattern = new RegExp('/plan-feature(\\?.*)?$');
  let username: string;
  let password: string;
  const planFeatureSample = { label: 'amidst amidst', included: true, emphasised: true, displayOrder: 4804 };

  let planFeature;
  let carePlanTeaser;

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
      url: '/api/care-plan-teasers',
      body: {
        name: 'promptly',
        forWho: 'Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=',
        priceAmount: 10551.31,
        priceCurrency: 'pis',
        pricePeriod: 'geez',
        priceNote: 'jeopardise if because',
        featured: false,
        displayOrder: 14942,
        published: false,
      },
    }).then(({ body }) => {
      carePlanTeaser = body;
    });
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/plan-features+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/plan-features').as('postEntityRequest');
    cy.intercept('DELETE', '/api/plan-features/*').as('deleteEntityRequest');
  });

  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/care-plan-teasers', {
      statusCode: 200,
      body: [carePlanTeaser],
    });
  });

  afterEach(() => {
    if (planFeature) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/plan-features/${planFeature.id}`,
      }).then(() => {
        planFeature = undefined;
      });
    }
  });

  afterEach(() => {
    if (carePlanTeaser) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/care-plan-teasers/${carePlanTeaser.id}`,
      }).then(() => {
        carePlanTeaser = undefined;
      });
    }
  });

  it('PlanFeatures menu should load PlanFeatures page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('plan-feature');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('PlanFeature').should('exist');
    cy.url().should('match', planFeaturePageUrlPattern);
  });

  describe('PlanFeature page', () => {
    it('should have translated page title', () => {
      cy.visit(planFeaturePageUrl);
      cy.getEntityHeading('PlanFeature').should('not.contain', 'abofonsaPreviewApp.planFeature.home.title');
    });

    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(planFeaturePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create PlanFeature page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/plan-feature/new$'));
        cy.getEntityCreateUpdateHeading('PlanFeature');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', planFeaturePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/plan-features',
          body: {
            ...planFeatureSample,
            plan: carePlanTeaser,
          },
        }).then(({ body }) => {
          planFeature = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/plan-features+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [planFeature],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(planFeaturePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details PlanFeature page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('planFeature');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', planFeaturePageUrlPattern);
      });

      it('edit button click should load edit PlanFeature page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('PlanFeature');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', planFeaturePageUrlPattern);
      });

      it('edit button click should load edit PlanFeature page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('PlanFeature');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', planFeaturePageUrlPattern);
      });

      it('last delete button click should delete instance of PlanFeature', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('planFeature').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', planFeaturePageUrlPattern);

        planFeature = undefined;
      });
    });
  });

  describe('new PlanFeature page', () => {
    beforeEach(() => {
      cy.visit(planFeaturePageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('PlanFeature');
    });

    it('should create an instance of PlanFeature', () => {
      cy.get(`[data-cy="label"]`).type('drat westernize');
      cy.get(`[data-cy="label"]`).should('have.value', 'drat westernize');

      cy.get(`[data-cy="included"]`).should('not.be.checked');
      cy.get(`[data-cy="included"]`).click();
      cy.get(`[data-cy="included"]`).should('be.checked');

      cy.get(`[data-cy="emphasised"]`).should('not.be.checked');
      cy.get(`[data-cy="emphasised"]`).click();
      cy.get(`[data-cy="emphasised"]`).should('be.checked');

      cy.get(`[data-cy="displayOrder"]`).type('27147');
      cy.get(`[data-cy="displayOrder"]`).should('have.value', '27147');

      cy.get(`[data-cy="plan"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        planFeature = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', planFeaturePageUrlPattern);
    });
  });
});
