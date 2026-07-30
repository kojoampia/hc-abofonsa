import { entityDetailsBackButtonSelector, entityDetailsButtonSelector, entityTableSelector } from '../../support/entity';

describe('DataExportLog e2e test', () => {
  const dataExportLogPageUrl = '/data-export-log';
  const dataExportLogPageUrlPattern = new RegExp('/data-export-log(\\?.*)?$');
  let username: string;
  let password: string;

  let dataExportLog;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/data-export-logs+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/data-export-logs').as('postEntityRequest');
    cy.intercept('DELETE', '/api/data-export-logs/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (dataExportLog) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/data-export-logs/${dataExportLog.id}`,
      }).then(() => {
        dataExportLog = undefined;
      });
    }
  });

  it('DataExportLogs menu should load DataExportLogs page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('data-export-log');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('DataExportLog').should('exist');
    cy.url().should('match', dataExportLogPageUrlPattern);
  });

  describe('DataExportLog page', () => {
    it('should have translated page title', () => {
      cy.visit(dataExportLogPageUrl);
      cy.getEntityHeading('DataExportLog').should('not.contain', 'abofonsaPreviewApp.dataExportLog.home.title');
    });

    describe('with existing value', () => {
      beforeEach(function () {
        cy.visit(dataExportLogPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details DataExportLog page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('dataExportLog');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', dataExportLogPageUrlPattern);
      });
    });
  });
});
