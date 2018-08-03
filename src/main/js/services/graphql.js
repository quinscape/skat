import Hub from "../services/hub"


/**
 * Logs graphql errors
 * @param errors
 */
export function defaultErrorHandler(errors)
{
    console.error("GraphQL Request failed");
    console.table(errors);
}

/**
 * GraphQL query service over websocket
 *
 * @param {Object} params               Parameters
 * @param {String} params.query         query string
 * @param {Object} [params.variables]   query variables
 *
 * @returns {Promise<Object>} Promise resolving to query data
 */
export default function (params) {

    //console.log("QUERY: ", params);

    return Hub.request("GRAPHQL", params)
    .then(
        ({ data, errors}) => {

            //console.log("GRAPHQL received", data, errors);

            if (errors)
            {
                return  Promise.reject(errors);
            }
            return data;
        }
    );
}

// return fetch(
//         window.location.origin + contextPath + "/graphql",
//         {
//             method: "POST",
//             credentials: "same-origin",
//             headers: {
//                 "Content-Type": "application/json",
//
//                 // spring security enforces every POST request to carry a csrf token as either parameter or header
//                 [csrfToken.header] : csrfToken.value
//             },
//             body: JSON.stringify(params)
//         }
//     )
//     .then(response => response.json())
