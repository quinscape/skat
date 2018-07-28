import React from "react"
import bootstrap from "jsview-bootstrap"
import Icon from "./components/Icon";

bootstrap(
    function (initial) {

        const {contextPath, csrfToken} = initial;

        return (
            <div className="container">
                <div className="row">
                    <div className="col">
                        <h1>Error</h1>
                    </div>
                </div>
            </div>
        );
    },
    () => console.info("ready!")
);

