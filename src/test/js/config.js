import assert from "power-assert"
import proxyquire from "proxyquire"

describe("config()", function () {

    it("validates read-only property access", function () {

        const configModule = proxyquire("../../main/js/services/config", {
            "../util/intercept" : { "default" : () => true }
        });

        configModule.__initConfig({
            contextPath: "/test",
            authentication: {
                login : "TestUser",
                roles : ["ROLE_TEST"]
            },
            csrfToken: null
        });

        const config = configModule.default();
        assert(config.contextPath === "/test" );
        assert(config.authentication.login === "TestUser" );
        assert(config.csrfToken === null );

        assert.throws( () => {
            config.xxx = 1;
        }, /xxx/);

        assert.throws( () => {
            config.authentication.yyy = 1;
        }, /yyy/);

        assert.throws( () => {
            console.log(config.nonExistent);
        }, /Invalid config name 'nonExistent'/);

    });

    it("is neutral in production", function () {

        const configModule = proxyquire("../../main/js/services/config", {
            "../util/intercept" : { "default" : () => false }
        });

        configModule.__initConfig({
            contextPath: "/test",
            authentication: {
                login : "TestUser",
                roles : ["ROLE_TEST"]
            },
            csrfToken: null
        });

        const config = configModule.default();
        assert(config.contextPath === "/test" );
        assert(config.authentication.login === "TestUser" );
        assert(config.csrfToken === null );

        config.xxx = 1;

        assert(config.xxx === 1 );

        assert(config.nonExistent === undefined);

    });
});
