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

describe('CarePlanTeaser e2e test', () => {
  const carePlanTeaserPageUrl = '/care-plan-teaser';
  const carePlanTeaserPageUrlPattern = new RegExp('/care-plan-teaser(\\?.*)?$');
  let username: string;
  let password: string;
  const carePlanTeaserSample = {
    name: 'wretched rationalise from',
    forWho: 'Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=',
    priceAmount: 8273.37,
    priceCurrency: 'yah',
    pricePeriod: 'minty especially',
    featured: false,
    displayOrder: 17407,
    published: false,
  };

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
    cy.intercept('GET', '/api/care-plan-teasers+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/care-plan-teasers').as('postEntityRequest');
    cy.intercept('DELETE', '/api/care-plan-teasers/*').as('deleteEntityRequest');
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

  it('CarePlanTeasers menu should load CarePlanTeasers page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('care-plan-teaser');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('CarePlanTeaser').should('exist');
    cy.url().should('match', carePlanTeaserPageUrlPattern);
  });

  describe('CarePlanTeaser page', () => {
    it('should have translated page title', () => {
      cy.visit(carePlanTeaserPageUrl);
      cy.getEntityHeading('CarePlanTeaser').should('not.contain', 'abofonsaPreviewApp.carePlanTeaser.home.title');
    });

    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(carePlanTeaserPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create CarePlanTeaser page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/care-plan-teaser/new$'));
        cy.getEntityCreateUpdateHeading('CarePlanTeaser');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', carePlanTeaserPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/care-plan-teasers',
          body: carePlanTeaserSample,
        }).then(({ body }) => {
          carePlanTeaser = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/care-plan-teasers+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [carePlanTeaser],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(carePlanTeaserPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details CarePlanTeaser page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('carePlanTeaser');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', carePlanTeaserPageUrlPattern);
      });

      it('edit button click should load edit CarePlanTeaser page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('CarePlanTeaser');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', carePlanTeaserPageUrlPattern);
      });

      it('edit button click should load edit CarePlanTeaser page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('CarePlanTeaser');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', carePlanTeaserPageUrlPattern);
      });

      it('last delete button click should delete instance of CarePlanTeaser', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('carePlanTeaser').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', carePlanTeaserPageUrlPattern);

        carePlanTeaser = undefined;
      });
    });
  });

  describe('new CarePlanTeaser page', () => {
    beforeEach(() => {
      cy.visit(carePlanTeaserPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('CarePlanTeaser');
    });

    it('should create an instance of CarePlanTeaser', () => {
      cy.get(`[data-cy="name"]`).type('beneath');
      cy.get(`[data-cy="name"]`).should('have.value', 'beneath');

      cy.get(`[data-cy="forWho"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="forWho"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="priceAmount"]`).type('5929.19');
      cy.get(`[data-cy="priceAmount"]`).should('have.value', '5929.19');

      cy.get(`[data-cy="priceCurrency"]`).type('man');
      cy.get(`[data-cy="priceCurrency"]`).should('have.value', 'man');

      cy.get(`[data-cy="pricePeriod"]`).type('provided posh');
      cy.get(`[data-cy="pricePeriod"]`).should('have.value', 'provided posh');

      cy.get(`[data-cy="priceNote"]`).type('hunger scratchy soon');
      cy.get(`[data-cy="priceNote"]`).should('have.value', 'hunger scratchy soon');

      cy.get(`[data-cy="featured"]`).should('not.be.checked');
      cy.get(`[data-cy="featured"]`).click();
      cy.get(`[data-cy="featured"]`).should('be.checked');

      cy.get(`[data-cy="displayOrder"]`).type('9706');
      cy.get(`[data-cy="displayOrder"]`).should('have.value', '9706');

      cy.get(`[data-cy="published"]`).should('not.be.checked');
      cy.get(`[data-cy="published"]`).click();
      cy.get(`[data-cy="published"]`).should('be.checked');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        carePlanTeaser = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', carePlanTeaserPageUrlPattern);
    });
  });
});
