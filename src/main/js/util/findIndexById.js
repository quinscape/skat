import createIndexFinder from "./createIndexOf";

export default createIndexFinder(
    (foo, id) =>
        foo.id === id
);

