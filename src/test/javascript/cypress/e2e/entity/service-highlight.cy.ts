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

describe('ServiceHighlight e2e test', () => {
  const serviceHighlightPageUrl = '/service-highlight';
  const serviceHighlightPageUrlPattern = new RegExp('/service-highlight(\\?.*)?$');
  let username: string;
  let password: string;
  const serviceHighlightSample = { label: 'but', displayOrder: 27706 };

  let serviceHighlight;
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
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/care-service-teasers',
      body: {
        slug: 'nearly phew deliberately',
        name: 'scaffold apt',
        blurb: 'Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=',
        iconKey: 'cosset',
        availableOn: 'insignificant',
        displayOrder: 19951,
        published: false,
      },
    }).then(({ body }) => {
      careServiceTeaser = body;
    });
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/service-highlights+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/service-highlights').as('postEntityRequest');
    cy.intercept('DELETE', '/api/service-highlights/*').as('deleteEntityRequest');
  });

  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/care-service-teasers', {
      statusCode: 200,
      body: [careServiceTeaser],
    });
  });

  afterEach(() => {
    if (serviceHighlight) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/service-highlights/${serviceHighlight.id}`,
      }).then(() => {
        serviceHighlight = undefined;
      });
    }
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

  it('ServiceHighlights menu should load ServiceHighlights page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('service-highlight');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('ServiceHighlight').should('exist');
    cy.url().should('match', serviceHighlightPageUrlPattern);
  });

  describe('ServiceHighlight page', () => {
    it('should have translated page title', () => {
      cy.visit(serviceHighlightPageUrl);
      cy.getEntityHeading('ServiceHighlight').should('not.contain', 'abofonsaPreviewApp.serviceHighlight.home.title');
    });

    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(serviceHighlightPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create ServiceHighlight page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/service-highlight/new$'));
        cy.getEntityCreateUpdateHeading('ServiceHighlight');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', serviceHighlightPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/service-highlights',
          body: {
            ...serviceHighlightSample,
            service: careServiceTeaser,
          },
        }).then(({ body }) => {
          serviceHighlight = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/service-highlights+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [serviceHighlight],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(serviceHighlightPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details ServiceHighlight page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('serviceHighlight');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', serviceHighlightPageUrlPattern);
      });

      it('edit button click should load edit ServiceHighlight page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ServiceHighlight');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', serviceHighlightPageUrlPattern);
      });

      it('edit button click should load edit ServiceHighlight page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ServiceHighlight');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', serviceHighlightPageUrlPattern);
      });

      it('last delete button click should delete instance of ServiceHighlight', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('serviceHighlight').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', serviceHighlightPageUrlPattern);

        serviceHighlight = undefined;
      });
    });
  });

  describe('new ServiceHighlight page', () => {
    beforeEach(() => {
      cy.visit(serviceHighlightPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('ServiceHighlight');
    });

    it('should create an instance of ServiceHighlight', () => {
      cy.get(`[data-cy="label"]`).type('so unhappy');
      cy.get(`[data-cy="label"]`).should('have.value', 'so unhappy');

      cy.get(`[data-cy="displayOrder"]`).type('13779');
      cy.get(`[data-cy="displayOrder"]`).should('have.value', '13779');

      cy.get(`[data-cy="service"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        serviceHighlight = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', serviceHighlightPageUrlPattern);
    });
  });
});
