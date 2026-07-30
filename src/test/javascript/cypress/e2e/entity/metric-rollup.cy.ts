import { entityDetailsBackButtonSelector, entityDetailsButtonSelector, entityTableSelector } from '../../support/entity';

describe('MetricRollup e2e test', () => {
  const metricRollupPageUrl = '/metric-rollup';
  const metricRollupPageUrlPattern = new RegExp('/metric-rollup(\\?.*)?$');
  let username: string;
  let password: string;

  let metricRollup;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/metric-rollups+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/metric-rollups').as('postEntityRequest');
    cy.intercept('DELETE', '/api/metric-rollups/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (metricRollup) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/metric-rollups/${metricRollup.id}`,
      }).then(() => {
        metricRollup = undefined;
      });
    }
  });

  it('MetricRollups menu should load MetricRollups page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('metric-rollup');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('MetricRollup').should('exist');
    cy.url().should('match', metricRollupPageUrlPattern);
  });

  describe('MetricRollup page', () => {
    it('should have translated page title', () => {
      cy.visit(metricRollupPageUrl);
      cy.getEntityHeading('MetricRollup').should('not.contain', 'abofonsaPreviewApp.metricRollup.home.title');
    });

    describe('with existing value', () => {
      beforeEach(function () {
        cy.visit(metricRollupPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details MetricRollup page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('metricRollup');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', metricRollupPageUrlPattern);
      });
    });
  });
});
