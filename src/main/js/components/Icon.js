import React from "react"
import PropTypes from "prop-types"
import cx from "classnames"

/**
 * Font-Awesome solid icon.
 */
class Icon extends React.Component {

    static propTypes = {
        className: PropTypes.string,
        title: PropTypes.string,
    };

    render()
    {
        const { className, title } = this.props;

        return (
            <i className={ cx("fas", className) } title={ title } />
        )
    }
}
export default Icon
