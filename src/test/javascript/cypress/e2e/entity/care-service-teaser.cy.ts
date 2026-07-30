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

describe('CareServiceTeaser e2e test', () => {
  const careServiceTeaserPageUrl = '/care-service-teaser';
  const careServiceTeaserPageUrlPattern = new RegExp('/care-service-teaser(\\?.*)?$');
  let username: string;
  let password: string;
  const careServiceTeaserSample = {
    slug: 'vacantly greatly',
    name: 'partridge',
    blurb: 'Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=',
    displayOrder: 28932,
    published: false,
  };

  let careServiceTeaser;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/care-service-teasers+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/care-service-teasers').as('postEntityRequest');
    cy.intercept('DELETE', '/api/care-service-teasers/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (careServiceTeaser) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/care-service-teasers/${careServiceTeaser.id}`,
      }).then(() => {
        careServiceTeaser = undefined;
      });
    }
  });

  it('CareServiceTeasers menu should load CareServiceTeasers page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('care-service-teaser');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('CareServiceTeaser').should('exist');
    cy.url().should('match', careServiceTeaserPageUrlPattern);
  });

  describe('CareServiceTeaser page', () => {
    it('should have translated page title', () => {
      cy.visit(careServiceTeaserPageUrl);
      cy.getEntityHeading('CareServiceTeaser').should('not.contain', 'abofonsaPreviewApp.careServiceTeaser.home.title');
    });

    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(careServiceTeaserPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create CareServiceTeaser page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/care-service-teaser/new$'));
        cy.getEntityCreateUpdateHeading('CareServiceTeaser');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', careServiceTeaserPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/care-service-teasers',
          body: careServiceTeaserSample,
        }).then(({ body }) => {
          careServiceTeaser = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/care-service-teasers+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [careServiceTeaser],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(careServiceTeaserPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details CareServiceTeaser page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('careServiceTeaser');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', careServiceTeaserPageUrlPattern);
      });

      it('edit button click should load edit CareServiceTeaser page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('CareServiceTeaser');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', careServiceTeaserPageUrlPattern);
      });

      it('edit button click should load edit CareServiceTeaser page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('CareServiceTeaser');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', careServiceTeaserPageUrlPattern);
      });

      it('last delete button click should delete instance of CareServiceTeaser', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('careServiceTeaser').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', careServiceTeaserPageUrlPattern);

        careServiceTeaser = undefined;
      });
    });
  });

  describe('new CareServiceTeaser page', () => {
    beforeEach(() => {
      cy.visit(careServiceTeaserPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('CareServiceTeaser');
    });

    it('should create an instance of CareServiceTeaser', () => {
      cy.get(`[data-cy="slug"]`).type('saw while');
      cy.get(`[data-cy="slug"]`).should('have.value', 'saw while');

      cy.get(`[data-cy="name"]`).type('though fort');
      cy.get(`[data-cy="name"]`).should('have.value', 'though fort');

      cy.get(`[data-cy="blurb"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="blurb"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="iconKey"]`).type('legislature worth');
      cy.get(`[data-cy="iconKey"]`).should('have.value', 'legislature worth');

      cy.get(`[data-cy="availableOn"]`).type('direct solemnly asset');
      cy.get(`[data-cy="availableOn"]`).should('have.value', 'direct solemnly asset');

      cy.get(`[data-cy="displayOrder"]`).type('15732');
      cy.get(`[data-cy="displayOrder"]`).should('have.value', '15732');

      cy.get(`[data-cy="published"]`).should('not.be.checked');
      cy.get(`[data-cy="published"]`).click();
      cy.get(`[data-cy="published"]`).should('be.checked');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        careServiceTeaser = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', careServiceTeaserPageUrlPattern);
    });
  });
});
