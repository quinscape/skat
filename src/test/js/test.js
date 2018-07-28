import assert from "power-assert"

describe("Testing", function(){

    it("Mocha is integrated", function()
	{
		assert(true);
	});

	it("Power Assert works", function()
	{
        const foo = "abc";
        const bar = 123;

        try
        {
            assert(foo === bar);
        }
        catch(e)
        {
            // error contains our string value, knows about the context
            assert(/"abc"/.test(e));
            return
        }
		assert(false);
	});
});
