import React from "react";
import cx from "classnames"
import Icon from "../Icon";

import {
    Field,
} from "formik"

export default function MultiplierRow(props)
{
    const { values, isValid, errors, handleChange, handleBlur, increaseMultiplier, decreaseMultiplier } = props;

    const { gameType } = values;

    const errorOnMultiplier = errors && errors.multiplier;

    return <div
        className={
            cx(
                "form-group row",
                errorOnMultiplier && "has-error",
            )
        }>
        <div className="col-sm-1"/>
        <label htmlFor="additional-multiplier" className="col-sm-2 col-form-label">Multiplier</label>
        <div className="col-sm-7">
            <div
                className="input-group"
            >
                <div
                    className="input-group-prepend"
                >
                    <button
                        className="btn btn-outline-secondary"
                        type="button"
                        onClick={ decreaseMultiplier }
                        disabled={ !isValid || gameType === "7" }
                    >
                        <Icon className="fa-minus"/>
                    </button>
                </div>
                <Field
                    id="additional-multiplier"
                    name="multiplier"
                    className={
                        cx(
                            "form-control",
                            errorOnMultiplier && "is-invalid"
                        )
                    }
                    value={ values.multiplier }
                    onChange={ handleChange }
                    onBlur={ handleBlur }
                    disabled={ gameType === "7" }
                />
                <div className="input-group-append">
                    <button
                        className="btn btn-outline-secondary"
                        type="button"
                        onClick={ increaseMultiplier }
                        disabled={ gameType === "7" }
                    >
                        <Icon className="fa-plus"/>
                    </button>
                </div>
            </div>
            {
                errorOnMultiplier && <p className="invalid-feedback">
                    Invalid multiplier
                </p>

            }
        </div>
        <div className="col-sm-1"/>
    </div>;
}
