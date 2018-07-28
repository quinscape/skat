import React from "react"
import Icon from "../../components/Icon";


/**
 * Protected route. While the Spring security roles are routed through to the entry point, it is often
 * preferable to control access control directly on the Spring Web MVC entry point or the general Spring security
 * configuration.
 *
 * You can also split the authenticated parts into their own end point, of course.
 */
class Admin extends React.Component {

    render()
    {
        return (
            <div>
                <h1> Admin </h1>
                <p>
                    <Icon className="text-info fa-info-circle"/> Protected route
                </p>
            </div>
        )
    }
}

export default Admin
