import { entityDetailsBackButtonSelector, entityDetailsButtonSelector, entityTableSelector } from '../../support/entity';

describe('CaptureEvent e2e test', () => {
  const captureEventPageUrl = '/capture-event';
  const captureEventPageUrlPattern = new RegExp('/capture-event(\\?.*)?$');
  let username: string;
  let password: string;

  let captureEvent;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/capture-events+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/capture-events').as('postEntityRequest');
    cy.intercept('DELETE', '/api/capture-events/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (captureEvent) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/capture-events/${captureEvent.id}`,
      }).then(() => {
        captureEvent = undefined;
      });
    }
  });

  it('CaptureEvents menu should load CaptureEvents page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('capture-event');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('CaptureEvent').should('exist');
    cy.url().should('match', captureEventPageUrlPattern);
  });

  describe('CaptureEvent page', () => {
    it('should have translated page title', () => {
      cy.visit(captureEventPageUrl);
      cy.getEntityHeading('CaptureEvent').should('not.contain', 'abofonsaPreviewApp.captureEvent.home.title');
    });

    describe('with existing value', () => {
      beforeEach(function () {
        cy.visit(captureEventPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details CaptureEvent page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('captureEvent');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', captureEventPageUrlPattern);
      });
    });
  });
});
