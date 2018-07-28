import graphql, { defaultErrorHandler } from "../../services/graphql";
import {
    getFooRowCount,
    getFoos
} from "../reducers";
import findIndexbyId from "../../util/findIndexById";

export const FOO_SET_LIST = "FOO_SET_LIST";
export const FOO_UPDATE_DETAIL = "FOO_UPDATE_DETAIL";

export function setFoos(foos, rowCount)
{
    return ({
        type: FOO_SET_LIST,
        foos,
        rowCount
    });
}

export const FOO_PAGING = 20;

// language=GraphQL
const LIST_FOOS_QUERY = `
    query listFoos($offset: Int, $limit : Int)
    {
        listFoos(offset: $offset, limit: $limit)
        {
            foos
            {
                id
                name
            }
            rowCount
        }
    }
`;

export function loadFoos(offset)
{
    return dispatch => graphql({
            query: LIST_FOOS_QUERY,
        variables: {
            offset,
            limit: FOO_PAGING
        }
    }
    ).then(
        data => {

            const { foos, rowCount } = data.listFoos;
            dispatch(
                setFoos(foos, rowCount)
            );
        },
        defaultErrorHandler
    );
}


// language=GraphQL
const FOO_DETAIL_QUERY = `
    query fooDetail($id: String)
    {
        fooDetail(id: $id)
        {
            id
            name
            description
            num
            typeId
            type {
                name
            }
            ownerId

            owner {
                login
            }
            created
        }
    }
`;

// language=GraphQL
const FOO_STORE_QUERY = `
    mutation fooDetail($foo: FooInput!)
    {
        storeFoo(foo: $foo)    
    }
`;


export function loadFooDetail(id)
{
    console.log("Load Foo detail", id );

    return dispatch => graphql({
            query: FOO_DETAIL_QUERY,
            variables: {
                id
            }
        })
        .then( ({ fooDetail }) => {

            console.log("Received", fooDetail);
            dispatch(
                updateFooDetail(fooDetail)
            );
            return fooDetail;
        }, defaultErrorHandler);
}

export function updateFooDetail(foo)
{
    return {
        type: FOO_UPDATE_DETAIL,
        foo
    };
}

export function storeFoo(foo)
{
    return ( dispatch, getState  ) => graphql({
        query: FOO_STORE_QUERY,
        variables: {
            foo
        }
    })
    .then(
        ( result ) => {

            const state = getState();

            const foos = getFoos(state);

            const index = findIndexbyId(foos, foo.id);
            if (index >= 0 && foos[index].name !== foo.name)
            {
                const newFoos = [ ... foos];

                // shortcut: full object fulfills slice criteria
                newFoos[index] = foo;

                dispatch(
                    setFoos(newFoos, getFooRowCount(state))
                )
            }
        },
        defaultErrorHandler
    );
}

