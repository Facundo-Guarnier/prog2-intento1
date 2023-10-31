import {
  entityTableSelector,
  entityDetailsButtonSelector,
  entityDetailsBackButtonSelector,
  entityCreateButtonSelector,
  entityCreateSaveButtonSelector,
  entityCreateCancelButtonSelector,
  entityEditButtonSelector,
  entityDeleteButtonSelector,
  entityConfirmDeleteButtonSelector,
} from '../../support/entity';

describe('Orden e2e test', () => {
  const ordenPageUrl = '/orden';
  const ordenPageUrlPattern = new RegExp('/orden(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const ordenSample = {};

  let orden;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/ordens+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/ordens').as('postEntityRequest');
    cy.intercept('DELETE', '/api/ordens/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (orden) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/ordens/${orden.id}`,
      }).then(() => {
        orden = undefined;
      });
    }
  });

  it('Ordens menu should load Ordens page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('orden');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Orden').should('exist');
    cy.url().should('match', ordenPageUrlPattern);
  });

  describe('Orden page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(ordenPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Orden page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/orden/new$'));
        cy.getEntityCreateUpdateHeading('Orden');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', ordenPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/ordens',
          body: ordenSample,
        }).then(({ body }) => {
          orden = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/ordens+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [orden],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(ordenPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Orden page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('orden');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', ordenPageUrlPattern);
      });

      it('edit button click should load edit Orden page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Orden');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', ordenPageUrlPattern);
      });

      it('edit button click should load edit Orden page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Orden');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', ordenPageUrlPattern);
      });

      it('last delete button click should delete instance of Orden', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('orden').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', ordenPageUrlPattern);

        orden = undefined;
      });
    });
  });

  describe('new Orden page', () => {
    beforeEach(() => {
      cy.visit(`${ordenPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Orden');
    });

    it('should create an instance of Orden', () => {
      cy.get(`[data-cy="cliente"]`).type('66342').should('have.value', '66342');

      cy.get(`[data-cy="accionId"]`).type('96603').should('have.value', '96603');

      cy.get(`[data-cy="accion"]`).type('Metal Lituania Avon').should('have.value', 'Metal Lituania Avon');

      cy.get(`[data-cy="operacion"]`).type('Gerente Representante').should('have.value', 'Gerente Representante');

      cy.get(`[data-cy="precio"]`).type('40074').should('have.value', '40074');

      cy.get(`[data-cy="cantidad"]`).type('77398').should('have.value', '77398');

      cy.get(`[data-cy="fechaOperacion"]`).type('Teclado y e-enable').should('have.value', 'Teclado y e-enable');

      cy.get(`[data-cy="modo"]`).type('program compress').should('have.value', 'program compress');

      cy.get(`[data-cy="estado"]`).type('Violeta optical').should('have.value', 'Violeta optical');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        orden = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', ordenPageUrlPattern);
    });
  });
});
