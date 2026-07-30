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

describe('LaunchMilestone e2e test', () => {
  const launchMilestonePageUrl = '/launch-milestone';
  const launchMilestonePageUrlPattern = new RegExp('/launch-milestone(\\?.*)?$');
  let username: string;
  let password: string;
  const launchMilestoneSample = {
    phaseLabel: 'spellcheck apropos',
    title: 'gloss when',
    current: false,
    displayOrder: 26013,
    published: false,
  };

  let launchMilestone;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/launch-milestones+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/launch-milestones').as('postEntityRequest');
    cy.intercept('DELETE', '/api/launch-milestones/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (launchMilestone) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/launch-milestones/${launchMilestone.id}`,
      }).then(() => {
        launchMilestone = undefined;
      });
    }
  });

  it('LaunchMilestones menu should load LaunchMilestones page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('launch-milestone');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('LaunchMilestone').should('exist');
    cy.url().should('match', launchMilestonePageUrlPattern);
  });

  describe('LaunchMilestone page', () => {
    it('should have translated page title', () => {
      cy.visit(launchMilestonePageUrl);
      cy.getEntityHeading('LaunchMilestone').should('not.contain', 'abofonsaPreviewApp.launchMilestone.home.title');
    });

    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(launchMilestonePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create LaunchMilestone page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/launch-milestone/new$'));
        cy.getEntityCreateUpdateHeading('LaunchMilestone');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', launchMilestonePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/launch-milestones',
          body: launchMilestoneSample,
        }).then(({ body }) => {
          launchMilestone = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/launch-milestones+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [launchMilestone],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(launchMilestonePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details LaunchMilestone page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('launchMilestone');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', launchMilestonePageUrlPattern);
      });

      it('edit button click should load edit LaunchMilestone page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('LaunchMilestone');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', launchMilestonePageUrlPattern);
      });

      it('edit button click should load edit LaunchMilestone page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('LaunchMilestone');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', launchMilestonePageUrlPattern);
      });

      it('last delete button click should delete instance of LaunchMilestone', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('launchMilestone').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', launchMilestonePageUrlPattern);

        launchMilestone = undefined;
      });
    });
  });

  describe('new LaunchMilestone page', () => {
    beforeEach(() => {
      cy.visit(launchMilestonePageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('LaunchMilestone');
    });

    it('should create an instance of LaunchMilestone', () => {
      cy.get(`[data-cy="phaseLabel"]`).type('minus duh refer');
      cy.get(`[data-cy="phaseLabel"]`).should('have.value', 'minus duh refer');

      cy.get(`[data-cy="title"]`).type('equally');
      cy.get(`[data-cy="title"]`).should('have.value', 'equally');

      cy.get(`[data-cy="body"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="body"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="milestoneDate"]`).type('2026-07-30');
      cy.get(`[data-cy="milestoneDate"]`).blur();
      cy.get(`[data-cy="milestoneDate"]`).should('have.value', '2026-07-30');

      cy.get(`[data-cy="current"]`).should('not.be.checked');
      cy.get(`[data-cy="current"]`).click();
      cy.get(`[data-cy="current"]`).should('be.checked');

      cy.get(`[data-cy="displayOrder"]`).type('25344');
      cy.get(`[data-cy="displayOrder"]`).should('have.value', '25344');

      cy.get(`[data-cy="published"]`).should('not.be.checked');
      cy.get(`[data-cy="published"]`).click();
      cy.get(`[data-cy="published"]`).should('be.checked');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        launchMilestone = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', launchMilestonePageUrlPattern);
    });
  });
});
