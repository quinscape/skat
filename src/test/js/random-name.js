import assert from "power-assert"
import proxyquire from "proxyquire"
import randomName from "../../main/js/util/randomName";

describe("randomName", function () {

    it("creates random names", function () {

        console.log(randomName());
        console.log(randomName());
        console.log(randomName());
        console.log(randomName());
        console.log(randomName());
    });
});
