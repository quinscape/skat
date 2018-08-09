import loadSymbols from "../util/loadSymbols";
import arrayEquals from "../util/arrayEquals";

let symbolPromise;

let mergedSymbols;

let files;

/**
 * Returns the already loaded symbols as synchronous value which of course only works when the main export was called
 * and resolved once already.
 * 
 * @return {*}
 */
export function getLoadedSymbols()
{
    return mergedSymbols;
}

/**
 * Loads all SVG files and returns a merged object with symbols from all files
 *
 * @param {Array<String>}svgFiles      array of SVG file URIs
 * 
 * @return {Promise<Object>} merged symbols
 */
export default function (svgFiles) {

    if (!Array.isArray(svgFiles))
    {

        throw new Error("Need array of SVG files URIs as first argument");
    }

    if (!symbolPromise)
    {
        files = svgFiles;
        symbolPromise = new Promise((resolve, reject) => {

            const promises = new Array(svgFiles.length);

            for (let i = 0; i < svgFiles.length; i++)
            {
                promises[i] = loadSymbols(svgFiles[i])
            }

            resolve(Promise.all(promises));

        }).then( array => {

            const merged = {};
            for (let i = 0; i < array.length; i++)
            {

                Object.assign(merged, array[i]);
            }

            mergedSymbols = merged;

            //console.log("Loaded ", merged);

            return merged;
        })
        .catch(err => console.error("ERROR", err));
    }
    else
    {
        if (!arrayEquals(files, svgFiles))
        {
            throw new Error("Loader() called with differing sets of SVG files: " + files + " and " + svgFiles);
        }
    }

    return symbolPromise;
}
