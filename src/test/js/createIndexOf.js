import assert from "power-assert"
import createIndexFinder from "../../main/js/util/createIndexOf"

describe("createIndexFinder", function () {

    it("creates findIndexBy* method based on a predicate", function () {

        const byValue = createIndexFinder( (foo, value) => foo === value );

        const array = ["A","B","C"];
        assert( byValue(array, "A") === 0);
        assert( byValue(array, "C") === 2);
        assert( byValue(array, "D") === -1);

    });
});
