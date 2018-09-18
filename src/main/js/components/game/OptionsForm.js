import React from "react"
import Field from "domainql-form/lib/Field";
import Select from "domainql-form/lib/Select";
import withForm from "domainql-form/lib/withForm";
import FormBlock from "domainql-form/lib/FormBlock";
import FieldMode from "domainql-form/lib/FieldMode";
import GlobalErrors from "domainql-form/lib/GlobalErrors";
import Icon from "../../components/Icon";
import config from "../../services/config";


class OptionsForm extends React.Component {

    render()
    {
        const { authentication } = config();

        const { formConfig, phase } = this.props;

        const { formikProps } = formConfig;

        const {isValid, values, errors} = formikProps;

        const disabled = phase !== "OPEN";

        return (
            <React.Fragment>
                <GlobalErrors/>
                
                <FormBlock mode={ FieldMode.disabledIf( disabled )}>

                    <Field name="allowContinue"/>
                    <Field name="allowRamsch"/>
                    <Field name="jackStraitsOnly"/>
                    <Select
                        name="shufflingStrategyName"
                        values={ config().shufflingStrategies.map( info => info.name ) }
                    />
                </FormBlock>
            </React.Fragment>
        );
    }
}

export default withForm(
    OptionsForm,
    {
        type: "GameOptionsInput"
    }
);
