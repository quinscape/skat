import React from "react"
import Icon from "../../components/Icon";


/**
 * Protected route. While the Spring security roles are routed through to the entry point, it is often
 * preferable to control access control directly on the Spring Web MVC entry point or the general Spring security
 * configuration.
 *
 * You can also split the authenticated parts into their own end point, of course.
 */
class AdminHome extends React.Component {

    render()
    {
        return (
            <div>
                <br/>
                <h1><Icon className="text-info fa-info-circle"/> Admin: Access controlled entry point </h1>
            </div>
        )
    }
}

export default AdminHome
